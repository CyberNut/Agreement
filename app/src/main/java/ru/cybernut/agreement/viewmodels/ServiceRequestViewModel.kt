package ru.cybernut.agreement.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.data.ApprovingRequestList
import ru.cybernut.agreement.db.AgreementsDatabase
import ru.cybernut.agreement.db.ServiceRequest
import ru.cybernut.agreement.network.KamiApi
import ru.cybernut.agreement.repositories.ServiceRequestRepository
import ru.cybernut.agreement.utils.RequestType


class ServiceRequestViewModel(application: Application, val request: ServiceRequest): AndroidViewModel(application) {

    private val TAG = "RequestViewModel"
    private val _status = MutableLiveData<KamiApiStatus>()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    var serviceRequest = MutableLiveData<ServiceRequest>()
    private var serviceRequestRepository: ServiceRequestRepository

    val status: LiveData<KamiApiStatus>
        get() = _status

    private var _confirmed = MutableLiveData<Boolean>(false)
    val confirmed: LiveData<Boolean>
        get() = _confirmed

    private var _needShowToast = MutableLiveData<Boolean>()
    val needShowToast: LiveData<Boolean>
        get() = _needShowToast

    init {
        val database = AgreementsDatabase.getDatabase(application)
        val serviceRequestDao = database.serviceRequestsDao()
        serviceRequestRepository = ServiceRequestRepository.getInstance(serviceRequestDao)
        serviceRequest.value = request
    }

    fun handleRequest(approve: Boolean) {
        //TODO: Обработка согласования
        val approvingRequestList = ApprovingRequestList(AgreementApp.loginCredential)
        approvingRequestList?.addRequestId(serviceRequest.value?.uuid!!)
        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<ApprovingRequestList> = moshi.adapter(ApprovingRequestList::class.java)
        val json: String = jsonAdapter.toJson(approvingRequestList)
        println(json)
        try {
            KamiApi.retrofitService.approveRequests(RequestType.SERVICE.toString(), approve, "Mobile application", json)
                .enqueue(object : Callback<Void> {
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.i(TAG, "ERROR Approve = $approve, Request = ${serviceRequest.value}")
                    }

                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Log.i(TAG, "SUCCESS Approve = $approve, Request = ${serviceRequest.value}")
                        coroutineScope.launch {
                            serviceRequestRepository.deleteRequest(
                                serviceRequest.value!!
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

