package ru.cybernut.agreement.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import ru.cybernut.agreement.db.AgreementsDatabase
import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.network.KamiAPIService
import ru.cybernut.agreement.network.KamiApi
import ru.cybernut.agreement.repositories.PaymentRequestRepository
import java.lang.Exception

enum class KamiApiStatus  { LOADING, ERROR, DONE }

class RequestListViewModel(application: Application): AndroidViewModel(application) {

    private val TAG = "RequestListViewModel"
    private val _status = MutableLiveData<KamiApiStatus>()

    lateinit var paymentRequestRepository: PaymentRequestRepository

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
        paymentRequestRepository = PaymentRequestRepository.getInstance(paymentRequestDao)
        _requests = paymentRequestRepository.getRequests()
        //updatePaymentRequests();
    }

    private fun updatePaymentRequests() = coroutineScope.async {
        try {
            val requests = KamiApi.retrofitService.getPaymentRequests("{\"password\":\"12345@qw)\",\"userName\":\"Калашник Ольга Георгиевна\"}").await()
            paymentRequestRepository.insertRequests(requests)
        } catch (e: Exception) {
            Log.i(TAG, "updatePaymentRequests", e)
        }
    }

    fun showPaymentRequest(request: PaymentRequest) {

    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

