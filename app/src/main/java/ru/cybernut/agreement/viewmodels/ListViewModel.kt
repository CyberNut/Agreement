package ru.cybernut.agreement.viewmodels

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.cybernut.agreement.data.Request
import ru.cybernut.agreement.repositories.Repository

class ListViewModel<T>(val repository: Repository<T>) : ViewModel() {

    private var _filter = MutableLiveData<String>("")
    val filter: LiveData<String>
        get() = _filter

    private var _requests : LiveData<List<T>> = _filter.switchMap {
        if (it.isEmpty()) {
            liveData {
                repository.getRequests(false)
            }
        } else {
            liveData {
                repository.getFilteredRequests(it)
            }
        }
    }

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
        viewModelScope.launch {
            _requests = repository.getRequests(true)
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
}