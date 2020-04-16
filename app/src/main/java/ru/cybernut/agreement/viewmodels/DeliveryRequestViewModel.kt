package ru.cybernut.agreement.viewmodels

import android.util.Log
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
import org.koin.core.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.data.ApprovingRequestList
import ru.cybernut.agreement.db.DeliveryRequest
import ru.cybernut.agreement.network.KamiApi
import ru.cybernut.agreement.repositories.DeliveryRequestRepository
import ru.cybernut.agreement.utils.KamiApiStatus
import ru.cybernut.agreement.utils.RequestType


class DeliveryRequestViewModel(val deliveryRequestRepository: DeliveryRequestRepository, val request: DeliveryRequest): ViewModel(), KoinComponent {

    private val TAG = "DelivRequestViewModel"
    private val _status = MutableLiveData<KamiApiStatus>()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    var deliveryRequest = MutableLiveData<DeliveryRequest>()

    val status: LiveData<KamiApiStatus>
        get() = _status

    private var _confirmed = MutableLiveData<Boolean>(false)
    val confirmed: LiveData<Boolean>
        get() = _confirmed

    private var _needShowToast = MutableLiveData<Boolean>()
    val needShowToast: LiveData<Boolean>
        get() = _needShowToast

    init {
        deliveryRequest.value = request
    }

    fun handleRequest(approve: Boolean, comment: String) {
        //TODO: Обработка согласования
        val approvingRequestList = ApprovingRequestList(AgreementApp.loginCredential)
        approvingRequestList?.addRequestId(deliveryRequest.value?.uuid!!)
        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<ApprovingRequestList> = moshi.adapter(ApprovingRequestList::class.java)
        val json: String = jsonAdapter.toJson(approvingRequestList)
        val commentary = comment.trim() + " (Mobile)"
        try {
            KamiApi.retrofitService.approveRequests(RequestType.DELIVERY.toString(), approve, commentary, json)
                .enqueue(object : Callback<Void> {
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.i(TAG, "ERROR Approve = $approve, Request = ${deliveryRequest.value}")
                    }

                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Log.i(TAG, "SUCCESS Approve = $approve, Request = ${deliveryRequest.value}")
                        coroutineScope.launch {
                            deliveryRequestRepository.deleteRequest(
                                deliveryRequest.value!!
                            )
                        }
                    }
                })
            showToast()
            //Log.i(TAG, "Approve = $approve, Request = ${paymentRequest.value}")
        } catch (e: Exception) {
            Log.e(TAG, "KamiApi.retrofitService.approveRequests failure", e)
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

