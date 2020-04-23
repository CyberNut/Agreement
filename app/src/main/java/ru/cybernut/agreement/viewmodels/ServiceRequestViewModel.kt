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
import ru.cybernut.agreement.db.ServiceRequest
import ru.cybernut.agreement.network.KamiApi
import ru.cybernut.agreement.repositories.ServiceRequestRepository
import ru.cybernut.agreement.utils.KamiApiStatus
import ru.cybernut.agreement.utils.RequestType
import timber.log.Timber

class ServiceRequestViewModel(val serviceRequestRepository: ServiceRequestRepository, val request: ServiceRequest): ViewModel(), KoinComponent {

    private val _status = MutableLiveData<KamiApiStatus>()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    var serviceRequest = MutableLiveData<ServiceRequest>()

    val status: LiveData<KamiApiStatus>
        get() = _status

    private var _confirmed = MutableLiveData<Boolean>(false)
    val confirmed: LiveData<Boolean>
        get() = _confirmed

    private var _needShowToast = MutableLiveData<Boolean>()
    val needShowToast: LiveData<Boolean>
        get() = _needShowToast

    init {
        serviceRequest.value = request
    }

    fun handleRequest(approve: Boolean, comment: String) {
        //TODO: Обработка согласования
        val approvingRequestList = ApprovingRequestList(AgreementApp.loginCredential)
        approvingRequestList?.addRequestId(serviceRequest.value?.uuid!!)
        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<ApprovingRequestList> = moshi.adapter(ApprovingRequestList::class.java)
        val json: String = jsonAdapter.toJson(approvingRequestList)
        val commentary = comment.trim() + " (Mobile)"
        try {
            KamiApi.retrofitService.approveRequests(RequestType.SERVICE.toString(), approve, commentary, json)
                .enqueue(object : Callback<Void> {
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Timber.d("ERROR Approve = $approve, Request = ${serviceRequest.value}")
                    }

                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Timber.d("SUCCESS Approve = $approve, Request = ${serviceRequest.value}")
                        coroutineScope.launch {
                            serviceRequestRepository.deleteRequest(
                                serviceRequest.value!!
                            )
                        }
                    }
                })
            showToast()
        } catch (e: Exception) {
            Timber.d("KamiApi.retrofitService.approveRequests failure " + e.message)
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