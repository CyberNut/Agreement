package ru.cybernut.agreement.viewmodels

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.cybernut.agreement.data.Request
import ru.cybernut.agreement.repositories.Repository
import ru.cybernut.agreement.utils.ApprovalType
import timber.log.Timber

class RequestListViewModel<T: Request>(val repository: Repository<T>) : ViewModel() {

    private var _filter = MutableLiveData<String>("")
    val filter: LiveData<String>
        get() = _filter

    private var _requests : LiveData<List<T>> = _filter.switchMap {
        if (it.isEmpty()) {
            repository.getRequests()
        } else {
            repository.getFilteredRequests(it)
        }
    }

    private var _approveResult: MutableLiveData<ApprovalType> = MutableLiveData(ApprovalType.NONE)
    val approveResult: LiveData<ApprovalType>
        get() = _approveResult

    private var _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    val requests: LiveData<List<T>>
        get() = _requests

    private val _navigateToSelectedRequest = MutableLiveData<Request>()
    val navigateToSelectedRequest: LiveData<Request>
        get() = _navigateToSelectedRequest

    val empty: LiveData<Boolean> = Transformations.map(_requests) {
        it.isEmpty()
    }

    init {
        forceUpdateRequests()
    }

    fun forceUpdateRequests() {
        try {
            viewModelScope.launch {
                _isLoading.postValue(true)
                repository.fetchRequests()
                _isLoading.postValue(false)
            }

        } catch (e: Exception) {
            Timber.d("fetchRequest() Error:" +  e.message)
        }
    }

    fun setFilter(newFilter: String) {
        _filter.value = newFilter
    }

    fun showRequest(request: Request) {
        _navigateToSelectedRequest.value = request
    }

    fun navigateToSelectedRequestComplete() {
        _navigateToSelectedRequest.value = null
    }

    fun onApproveRequestDone() {
        _approveResult.value = ApprovalType.NONE
    }

    fun approveSelected(listOfRequest: List<String>) {
        viewModelScope.launch {
            _approveResult.value = repository.handleRequest(true, "", listOfRequest)
        }
        Timber.d("approveResult = " + approveResult.value)
    }
}