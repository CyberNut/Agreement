package ru.cybernut.agreement.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.data.Request
import ru.cybernut.agreement.db.AgreementsDatabase
import ru.cybernut.agreement.db.ServiceRequest
import ru.cybernut.agreement.network.KamiApi
import ru.cybernut.agreement.repositories.ServiceRequestRepository


class ServiceRequestListViewModel(application: Application): AndroidViewModel(application)  {

    private val TAG = "ServiceRequestListViewModel"
    private var database: AgreementsDatabase
    private var serviceRequestRepository: ServiceRequestRepository

    private var viewModelJob = Job()
    protected val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _navigateToSelectedRequest = MutableLiveData<Request>()
    val navigateToSelectedRequest: LiveData<Request>
        get() = _navigateToSelectedRequest

    private var _requests : LiveData<List<ServiceRequest>>
    val requests: LiveData<List<ServiceRequest>>
        get() = _requests

    init {
        database = AgreementsDatabase.getDatabase(application)
        val serviceRequestDao = database.serviceRequestsDao()
        serviceRequestRepository = ServiceRequestRepository.getInstance(serviceRequestDao)
        _requests = serviceRequestRepository.getRequests()
        updateRequests()
    }

    @SuppressLint("LongLogTag")
    fun updateRequests() = coroutineScope.async {
        try {
            val credential = AgreementApp.loginCredential
            val requests = KamiApi.retrofitService.getServiceRequests("{\"password\":\"" + credential.password + "\",\"userName\":\"" + credential.userName + "\"}").await()
            serviceRequestRepository.deleteAllRequests()
            serviceRequestRepository.insertRequests(requests)
        } catch (e: Exception) {
            Log.i(TAG, "updatePaymentRequests", e)
        }
    }

    fun showRequest(request: Request) {
        _navigateToSelectedRequest.value = request
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun navigateToSelectedRequestComplete() {
        _navigateToSelectedRequest.value = null
    }
}
