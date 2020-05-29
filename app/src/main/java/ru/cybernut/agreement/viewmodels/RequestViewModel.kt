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
import ru.cybernut.agreement.data.Request
import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.network.KamiApi
import ru.cybernut.agreement.repositories.Repository
import ru.cybernut.agreement.utils.ApprovalType
import ru.cybernut.agreement.utils.KamiApiStatus
import ru.cybernut.agreement.utils.RequestType
import timber.log.Timber


class RequestViewModel<T: Request>(private val repository: Repository<T>, val request: T): ViewModel() {

    private val _status = MutableLiveData<KamiApiStatus>()

    var paymentRequest = MutableLiveData<T>()

    val status: LiveData<KamiApiStatus>
        get() = _status

    private var _approveResult = MutableLiveData<ApprovalType>(ApprovalType.NONE)
    val approveResult: LiveData<ApprovalType>
        get() = _approveResult

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
                        _approveResult.value = ApprovalType.ERROR
                    }

                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Timber.d("SUCCESS Approve = $approve, Request = ${paymentRequest.value}")
                        coroutineScope.launch {
//                            paymentRequestRepository.deleteRequest(
//                                paymentRequest.value!!
//                            )
                        }
                        _approveResult.value = if (approve) ApprovalType.APPROVE else ApprovalType.DECLINE
                    }
                })
        } catch (e: Exception) {
            Timber.d( "KamiApi.retrofitService.approveRequests failure " + e.message)
            _approveResult.value = ApprovalType.ERROR
        }
    }

    fun onApproveRequestDone() {
        _approveResult.value = ApprovalType.NONE
    }
}

