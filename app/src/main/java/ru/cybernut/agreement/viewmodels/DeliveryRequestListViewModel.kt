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
import ru.cybernut.agreement.db.DeliveryRequest
import ru.cybernut.agreement.network.KamiApi
import ru.cybernut.agreement.repositories.DeliveryRequestRepository


class DeliveryRequestListViewModel(application: Application): AndroidViewModel(application)  {

    private val TAG = "DelivRequestListVM"
    private var database: AgreementsDatabase
    private var deliveryRequestRepository: DeliveryRequestRepository

    private var viewModelJob = Job()
    protected val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _navigateToSelectedRequest = MutableLiveData<Request>()
    val navigateToSelectedRequest: LiveData<Request>
        get() = _navigateToSelectedRequest

    private var _requests : LiveData<List<DeliveryRequest>>
    val requests: LiveData<List<DeliveryRequest>>
        get() = _requests

    init {
        database = AgreementsDatabase.getDatabase(application)
        val deliveryRequestDao = database.deliveryRequestsDao()
        deliveryRequestRepository = DeliveryRequestRepository.getInstance(deliveryRequestDao)
        _requests = deliveryRequestRepository.getRequests()
        updateRequests()
    }

    fun updateRequests() = coroutineScope.async {
        try {
            val credential = AgreementApp.loginCredential
            val requests = KamiApi.retrofitService.getDeliveryRequests("{\"password\":\"" + credential.password + "\",\"userName\":\"" + credential.userName + "\"}").await()
            Log.i(TAG, "size=" + requests.size)
            deliveryRequestRepository.deleteAllRequests()
            deliveryRequestRepository.insertRequests(requests)
        } catch (e: Exception) {
            Log.i(TAG, "updateDeliveryRequests", e)
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
