package ru.cybernut.agreement.viewmodels

import android.app.Application
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.R
import ru.cybernut.agreement.data.LoginCredential
import ru.cybernut.agreement.network.KamiApi
import ru.cybernut.agreement.utils.KamiApiStatus
import timber.log.Timber
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

class LoginViewModel(application: Application): AndroidViewModel(application) {

    private val _status = MutableLiveData<KamiApiStatus>()
    val status: LiveData<KamiApiStatus>
        get() = _status

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _loginSuccess = MutableLiveData<Boolean>(false)
    val loginSuccess: LiveData<Boolean>
        get() = _loginSuccess

    private val _incorrectLogin = MutableLiveData<String>("")
    val incorrectLogin: LiveData<String>
        get() = _incorrectLogin

    fun navigateToRequestListDone() {
        _loginSuccess.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun encryptString(source: String): String? {
        var encryptedPwd: String? = ""
        try {
            val keySpec =
                DESKeySpec("as;p953z][03948".toByteArray(charset("UTF8")))
            val keyFactory =
                SecretKeyFactory.getInstance("DES")
            val key = keyFactory.generateSecret(keySpec)
            val cleartext = source.toByteArray(charset("UTF8"))
            val cipher =
                Cipher.getInstance("DES") // cipher is not thread safe
            cipher.init(Cipher.ENCRYPT_MODE, key)
            encryptedPwd = Base64.encodeToString(
                cipher.doFinal(cleartext),
                Base64.DEFAULT
            )
        } catch (e: Exception) {
            Timber.d( "encryptString: " + e.message)
        }
        return encryptedPwd
    }

    fun decryptString(source: String?): String? {
        var decryptedPwd = ""
        try {
            val keySpec =
                DESKeySpec("as;p953z][03948".toByteArray(charset("UTF8")))
            val keyFactory =
                SecretKeyFactory.getInstance("DES")
            val key = keyFactory.generateSecret(keySpec)
            val encrypedPwdBytes =
                Base64.decode(source, Base64.DEFAULT)
            val cipher =
                Cipher.getInstance("DES") // cipher is not thread safe
            cipher.init(Cipher.DECRYPT_MODE, key)
            val plainTextPwdBytes = cipher.doFinal(encrypedPwdBytes)
            decryptedPwd = String(plainTextPwdBytes)
        } catch (e: Exception) {
            Timber.d( "decryptString: " + e.message)
        }
        return decryptedPwd
    }

    fun doLogin(userName: String, password: String) {
        try {
            Timber.d("Before calling retrofit")
            KamiApi.retrofitService.doLogin("User=" + userName + ";Pswd=" + password)
                .enqueue(
                    object : Callback<Void> {
                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Timber.d("doLogin onFailure")
                            _incorrectLogin.value = getApplication<AgreementApp>().resources.getString(R.string.error_connection)
                        }

                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            when (response.code()) {
                                200 -> {
                                    Timber.d( "doLogin success")
                                    _incorrectLogin.value = ""
                                    _loginSuccess.value = true
                                }
                                401 -> {
                                    Timber.d( "incorrect login (401)")
                                    _incorrectLogin.value = "incorrect login (401)"
                                }
                                else -> {
                                    Timber.d( "connection failure")
                                    _incorrectLogin.value = "error code = " + response.code()
                                }
                            }
                            AgreementApp.loginCredential = LoginCredential(userName, password)
                        }
                    }
                )
        } catch (e: java.lang.Exception) {
            Timber.d("updatePaymentRequests" + e.message)
        }
    }

}

