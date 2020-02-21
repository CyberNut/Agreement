package ru.cybernut.agreement

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.coroutines.*
import ru.cybernut.agreement.databinding.ActivityLoginBinding
import ru.cybernut.agreement.utils.SimpleScannerActivity
import ru.cybernut.agreement.utils.hideKeyboard
import ru.cybernut.agreement.viewmodels.LoginViewModel

class LoginActivity : AppCompatActivity() {
    companion object {
        val QRCODE_STRING = "QRCODE_STRING"
    }
    private val TAG = "LoginActivity"
    private val ZBAR_CAMERA_PERMISSION = 1
    val QR_CODE_READING = 2

    private val AUTOLOGIN_DELAY_MILLIS = 2000L
    private val PREF_FILE_NAME = "Login"
    private val PREF_REMEMBER_FLAG = "REMEMBER_FLAG"
    private val PREF_AUTOLOGIN_FLAG = "AUTOLOGIN_FLAG"
    private val PREF_USER_NAME = "USER_NAME"
    private val PREF_PASSWORD = "PASSWORD"
    private var fragmentJob = Job()
    private val coroutineScope = CoroutineScope(fragmentJob + Dispatchers.Main)

    private val MIN_PASSWORD_LENGHT = 9

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by lazy {
        ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login)
        binding.setLifecycleOwner(this)

        binding.viewModel = viewModel

        viewModel.loginSuccess.observe(this, Observer {
            if(it) {
                saveSettings()
                Toast.makeText(this, resources.getString(R.string.authorization_successful), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                viewModel.navigateToRequestListDone()
                finish()
            }
        })

        viewModel.incorrectLogin.observe(this, Observer {
            if(it) {
                binding.errorLoginLabel.setVisibility(View.VISIBLE)
            } else {
                binding.errorLoginLabel.setVisibility(View.INVISIBLE)
            }
            showProgress(false)
        })

        binding.readQrCode.setOnClickListener {
            cancelLogin()
            readQRCode()
        }
        binding.signInButton.setOnClickListener {
            cancelLogin()
            attemptLogin()
        }

        loadSettings()
        setUIChangeListenerToCancelAutoLogin()
    }

    private fun setUIChangeListenerToCancelAutoLogin() {
        val textChangeListener = object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                cancelLogin()
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
        }
        val jobCanceler = object: View.OnClickListener {
            override fun onClick(v: View?) {
                cancelLogin()
            }
        }
        binding.login.addTextChangedListener(textChangeListener)
        binding.password.addTextChangedListener(textChangeListener)
        binding.autologinCheckbox.setOnClickListener(jobCanceler)
        binding.autologinCheckbox.setOnClickListener(jobCanceler)
        binding.rememberPasswordCheckbox.setOnClickListener(jobCanceler)
    }

    override fun onResume() {
        super.onResume()
        if(binding.autologinCheckbox.isChecked) {
            coroutineScope.launch{
                delay(AUTOLOGIN_DELAY_MILLIS)
                attemptLogin()
            }
        }
        if (binding.login.text.isNotEmpty()) {
            hideKeyboard()
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == QR_CODE_READING) {
                var qrString = data!!.getStringExtra(LoginActivity.QRCODE_STRING)
                qrString = qrString.replace("User=", "")
                //parse QR String for get user/pass
                val userName: String
                val password: String
                if (!qrString.isEmpty()) {
                    val loginEnd = qrString.indexOf(";Pswd=")
                    if (loginEnd >= 0) {
                        userName = qrString.substring(0, loginEnd)
                        password = qrString.substring(loginEnd + 6)
                        binding.login.setText(userName)
                        binding.password.setText(password)
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "Camera unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    fun cancelLogin() {
        fragmentJob.cancel()
        showProgress(false)
    }

    private fun readQRCode() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                ZBAR_CAMERA_PERMISSION
            )
        } else {
            val intent = Intent(this, SimpleScannerActivity::class.java)
            startActivityForResult(intent, QR_CODE_READING)
        }
    }

    private fun attemptLogin() {

        binding.errorLoginLabel.setVisibility(View.INVISIBLE)
        // Reset errors.
        binding.login.setError(null)
        binding.password.setError(null)

        val userName: String = binding.login.getText().toString()
        val password: String = binding.password.getText().toString()

        var cancel = false
        var focusView: View? = null

        if (password.isNotEmpty() && !isPasswordValid(password)) {
            binding.password.setError(getString(R.string.error_invalid_password))
            focusView = binding.password
            cancel = true
        } else if(password.isEmpty()) {
            binding.password.setError(getString(R.string.error_field_required))
            focusView = binding.password
            cancel = true
        }

        if (userName.isEmpty()) {
            binding.login.setError(getString(R.string.error_field_required))
            focusView = binding.login
            cancel = true
        }

        if (cancel) {
            focusView!!.requestFocus()
        } else {
            showProgress(true)
            coroutineScope.run {
                viewModel.doLogin(userName, password)
            }
        }
    }

    private fun showProgress(visible: Boolean) {
        if (visible) {
            binding.progressBar.setVisibility(View.VISIBLE)
        } else {
            binding.progressBar.setVisibility(View.GONE)
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= MIN_PASSWORD_LENGHT
    }

    private fun loadSettings() {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(
            PREF_FILE_NAME,
            Context.MODE_PRIVATE
        )
        val rememberFlag = sharedPreferences.getBoolean(
            PREF_REMEMBER_FLAG,
            false
        )
        if (rememberFlag) {
            binding.rememberPasswordCheckbox.setChecked(rememberFlag)
            val userName = sharedPreferences.getString(
                PREF_USER_NAME,
                ""
            )
            if (userName != "") {
                binding.login.setText(viewModel.decryptString(userName))
            }
            val password = sharedPreferences.getString(
                PREF_PASSWORD,
                ""
            )
            if (password != "") {
                binding.password.setText(viewModel.decryptString(password))
            }
        }
        val autoLoginFlag = sharedPreferences.getBoolean(
            PREF_AUTOLOGIN_FLAG,
            false
        )
        if (autoLoginFlag) {
            binding.autologinCheckbox.setChecked(autoLoginFlag)
        }
    }

    private fun saveSettings() {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(
            PREF_FILE_NAME,
            Context.MODE_PRIVATE
        )
        sharedPreferences.edit()
            .putBoolean(
                PREF_REMEMBER_FLAG,
                binding.rememberPasswordCheckbox.isChecked()
            )
            .putBoolean(
                PREF_AUTOLOGIN_FLAG,
                binding.autologinCheckbox.isChecked()
            )
            .putString(
                PREF_USER_NAME,
                viewModel.encryptString(binding.login.getText().toString())
            )
            .putString(
                PREF_PASSWORD,
                viewModel.encryptString(binding.password.getText().toString())
            )
            .apply()
    }
}
