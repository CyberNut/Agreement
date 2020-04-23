package ru.cybernut.agreement.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.data.ApprovingRequestList
import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.network.KamiApi
import ru.cybernut.agreement.repositories.PaymentRequestRepository
import ru.cybernut.agreement.utils.KamiApiStatus
import ru.cybernut.agreement.utils.RequestType
import timber.log.Timber


class PaymentRequestViewModel(val paymentRequestRepository: PaymentRequestRepository, val request: PaymentRequest): ViewModel(), KoinComponent {

    private val _status = MutableLiveData<KamiApiStatus>()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    var paymentRequest = MutableLiveData<PaymentRequest>()

    val status: LiveData<KamiApiStatus>
        get() = _status

    private var _confirmed = MutableLiveData<Boolean>(false)
    val confirmed: LiveData<Boolean>
        get() = _confirmed

    private var _needShowToast = MutableLiveData<Boolean>()
    val needShowToast: LiveData<Boolean>
        get() = _needShowToast

    init {
        paymentRequest.value = request
    }

    fun handleRequest(approve: Boolean, comment: String) {
        //TODO: Обработка согласования
        val approvingRequestList = ApprovingRequestList(AgreementApp.loginCredential)
        approvingRequestList?.addRequestId(paymentRequest.value?.uuid!!)
        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<ApprovingRequestList> = moshi.adapter(ApprovingRequestList::class.java)
        val json: String = jsonAdapter.toJson(approvingRequestList)
        val commentary = comment.trim() + " (Mobile)"
        try {
            KamiApi.retrofitService.approveRequests(RequestType.MONEY.toString(), approve, commentary, json)
                .enqueue(object : Callback<Void> {
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Timber.d("ERROR Approve = $approve, Request = ${paymentRequest.value}")
                    }

                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Timber.d("SUCCESS Approve = $approve, Request = ${paymentRequest.value}")
                        coroutineScope.launch {
                            paymentRequestRepository.deleteRequest(
                                paymentRequest.value!!
                            )
                        }
                    }
                })
            showToast()
        } catch (e: Exception) {
            Timber.d( "KamiApi.retrofitService.approveRequests failure " + e.message)
        }
    }

    fun onToastShowDone() {
        _needShowToast.value = false
    }

    fun showToast() {
        _needShowToast.value = true
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

