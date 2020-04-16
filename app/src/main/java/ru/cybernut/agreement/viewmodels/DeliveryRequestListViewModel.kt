package ru.cybernut.agreement.viewmodels

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.data.Request
import ru.cybernut.agreement.db.DeliveryRequest
import ru.cybernut.agreement.network.KamiApi
import ru.cybernut.agreement.repositories.DeliveryRequestRepository

class DeliveryRequestListViewModel(val deliveryRequestRepository: DeliveryRequestRepository): ViewModel()  {

    private val TAG = "DeliveryRqstListVM"

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

    val empty: LiveData<Boolean> = Transformations.map(_requests) {
        it.isEmpty()
    }

    init {
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
