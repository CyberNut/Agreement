package ru.cybernut.agreement.viewmodels

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import ru.cybernut.agreement.data.Request
import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.repositories.PaymentRequestRepository
import timber.log.Timber

class PaymentRequestListViewModel(val paymentRequestRepository: PaymentRequestRepository) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private var _filter = MutableLiveData<String>("")
    val filter: LiveData<String>
        get() = _filter

    private var _requests : LiveData<List<PaymentRequest>> = _filter.switchMap {
        if (it.isEmpty()) {
            paymentRequestRepository.getRequests()
        } else {
            paymentRequestRepository.getFilteredRequests(it)
        }
    }

    val requests: LiveData<List<PaymentRequest>>
        get() = _requests

    private val _navigateToSelectedRequest = MutableLiveData<Request>()
    val navigateToSelectedRequest: LiveData<Request>
        get() = _navigateToSelectedRequest

    val empty: LiveData<Boolean> = Transformations.map(_requests) {
        it.isEmpty()
    }

    init {
        Timber.d("init PaymentRequestViewModel")
        updateRequests()
    }

    fun updateRequests() = coroutineScope.async {
        Timber.d("updateRequests from PaymentRequestViewModel")
        paymentRequestRepository.updateRequests()
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
