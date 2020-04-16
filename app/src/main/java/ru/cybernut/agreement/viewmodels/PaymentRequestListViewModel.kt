package ru.cybernut.agreement.viewmodels

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.data.Request
import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.network.KamiApi
import ru.cybernut.agreement.repositories.PaymentRequestRepository

class PaymentRequestListViewModel(val paymentRequestRepository: PaymentRequestRepository) : ViewModel() {

    private val TAG = "PaymentRqstListVM"

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
        Log.i(TAG, "init")
        updateRequests()
    }

    fun updateRequests() = coroutineScope.async {
        try {
            val credential = AgreementApp.loginCredential
            val requests = KamiApi.retrofitService.getPaymentRequests("{\"password\":\"" + credential.password + "\",\"userName\":\"" + credential.userName + "\"}").await()
            _filter.value = ""
            paymentRequestRepository.deleteAllRequests()
            paymentRequestRepository.insertRequests(requests)
        } catch (e: Exception) {
            Log.i(TAG, "updatePaymentRequests", e)
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
