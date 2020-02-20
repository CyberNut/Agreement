package ru.cybernut.agreement.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
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

    private val TAG = "DeliveryRqstListVM"
    private var database: AgreementsDatabase
    private lateinit var deliveryRequestRepository: DeliveryRequestRepository

    private var viewModelJob = Job()
    protected val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _navigateToSelectedRequest = MutableLiveData<Request>()
    val navigateToSelectedRequest: LiveData<Request>
        get() = _navigateToSelectedRequest

    private var _filter = MutableLiveData<String>("")
    val filter: LiveData<String>
        get() = _filter

    private var _requests : LiveData<List<DeliveryRequest>> = _filter.switchMap {
        if (it.isEmpty()) {
            Log.i(TAG, "NO FILTER")
            deliveryRequestRepository.getRequests()
        } else {
            Log.i(TAG, "FILTER = " + it)
            deliveryRequestRepository.getFilteredRequests(it)
        }
    }

    val requests: LiveData<List<DeliveryRequest>>
        get() = _requests

    init {
        database = AgreementsDatabase.getDatabase(application)
        val deliveryRequestDao = database.deliveryRequestsDao()
        deliveryRequestRepository = DeliveryRequestRepository.getInstance(deliveryRequestDao)
        updateRequests()
    }

    fun updateRequests() = coroutineScope.async {
        try {
            val credential = AgreementApp.loginCredential
            val requests = KamiApi.retrofitService.getDeliveryRequests("{\"password\":\"" + credential.password + "\",\"userName\":\"" + credential.userName + "\"}").await()
            _filter.value = ""
            deliveryRequestRepository.insertRequests(requests)
        } catch (e: Exception) {
            Log.i(TAG, "updateDeliveryRequests", e)
        }
    }

    fun setFilter(newFilter: String) {
        _filter.value = newFilter
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
