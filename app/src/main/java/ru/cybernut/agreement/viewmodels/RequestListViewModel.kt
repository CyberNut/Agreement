package ru.cybernut.agreement.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import ru.cybernut.agreement.db.AgreementsDatabase
import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.network.KamiAPIService
import ru.cybernut.agreement.network.KamiApi
import ru.cybernut.agreement.repositories.PaymentRequestRepository

enum class KamiApiStatus  { LOADING, ERROR, DONE }

class RequestListViewModel(application: Application): AndroidViewModel(application) {

    private val _status = MutableLiveData<KamiApiStatus>()

    val status: LiveData<KamiApiStatus>
        get() = _status

    private var _requests : LiveData<List<PaymentRequest>>
    val requests: LiveData<List<PaymentRequest>>
        get() = _requests

    private var viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        val database = AgreementsDatabase.getDatabase(application)
        val paymentRequestDao = database.paymentRequestsDao()
        val paymentRequestRepository = PaymentRequestRepository.getInstance(paymentRequestDao)
        _requests = paymentRequestRepository.getRequests()
    }

    private fun getPaymentRequests() {
        KamiApi.retrofitService.getPaymentRequests("")
    }

    fun showPaymentRequest(request: PaymentRequest) {

    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

