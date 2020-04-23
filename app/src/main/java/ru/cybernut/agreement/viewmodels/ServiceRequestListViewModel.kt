package ru.cybernut.agreement.viewmodels

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.data.Request
import ru.cybernut.agreement.db.ServiceRequest
import ru.cybernut.agreement.network.KamiApi
import ru.cybernut.agreement.repositories.ServiceRequestRepository
import timber.log.Timber


class ServiceRequestListViewModel(val serviceRequestRepository: ServiceRequestRepository): ViewModel()  {

    private var viewModelJob = Job()
    protected val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _navigateToSelectedRequest = MutableLiveData<Request>()
    val navigateToSelectedRequest: LiveData<Request>
        get() = _navigateToSelectedRequest

    private var _filter = MutableLiveData<String>("")
    val filter: LiveData<String>
        get() = _filter

    private var _requests : LiveData<List<ServiceRequest>> =  _filter.switchMap {
        if (it.isEmpty()) {
            Timber.d("NO FILTER")
            serviceRequestRepository.getRequests()
        } else {
            Timber.d("FILTER = " + it)
            serviceRequestRepository.getFilteredRequests(it)
        }
    }
    val requests: LiveData<List<ServiceRequest>>
        get() = _requests

    val empty: LiveData<Boolean> = Transformations.map(_requests) {
        it.isEmpty()
    }

    init {
        Timber.d("init ServiceRequestViewModel")
        updateRequests()
    }

    fun updateRequests() = coroutineScope.async {
        Timber.d("updateRequests from ServiceRequestViewModel")
        serviceRequestRepository.updateRequests()
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
