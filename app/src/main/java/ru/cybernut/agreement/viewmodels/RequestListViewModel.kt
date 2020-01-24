package ru.cybernut.agreement.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.data.Request
import ru.cybernut.agreement.db.AgreementsDatabase
import ru.cybernut.agreement.db.BaseRequestDao
import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.db.ServiceRequest
import ru.cybernut.agreement.network.KamiApi
import ru.cybernut.agreement.repositories.PaymentRequestRepository
import ru.cybernut.agreement.repositories.RequestRepository
import ru.cybernut.agreement.repositories.ServiceRequestRepository
import ru.cybernut.agreement.utils.RequestType
import java.lang.Exception

enum class KamiApiStatus  { LOADING, ERROR, DONE }

class RequestListViewModel(application: Application, val requestType: RequestType): AndroidViewModel(application) {

    private val TAG = "RequestListViewModel"
    private val _status = MutableLiveData<KamiApiStatus>()

    private var paymentRequestRepository: PaymentRequestRepository? = null
    private var serviceRequestRepository: ServiceRequestRepository? = null
    lateinit var requestRepository: RequestRepository<BaseRequestDao<Request>>

    val status: LiveData<KamiApiStatus>
        get() = _status

    private var _requests : LiveData<List<PaymentRequest>>
    val requests: LiveData<List<PaymentRequest>>
        get() = _requests

    private var _serviceRequests : LiveData<List<ServiceRequest>>
    val serviceRequests: LiveData<List<ServiceRequest>>
        get() = _serviceRequests

    private val _navigateToSelectedRequest = MutableLiveData<Request>()
    val navigateToSelectedRequest: LiveData<Request>
        get() = _navigateToSelectedRequest

    private var viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        Log.i(TAG, "Init view model")
        val database = AgreementsDatabase.getDatabase(application)

        //requestRepository = RequestRepository(paymentRequestDao<PaymentRequest>)
        when(requestType) {
            RequestType.MONEY -> {
                val paymentRequestDao = database.paymentRequestsDao()
                paymentRequestRepository = PaymentRequestRepository.getInstance(paymentRequestDao)
                _requests = paymentRequestRepository.getRequests()
            }
            RequestType.SERVICE -> {
                val serviceRequestDao = database.serviceRequestsDao()
                serviceRequestRepository = ServiceRequestRepository.getInstance(serviceRequestDao)
                _serviceRequests = serviceRequestRepository.getRequests()
            }
            RequestType.DELIVERY -> {
                //_requests = paymentRequestRepository.getRequests()
            }
        }

        updateRequests();
    }

    fun updateRequests() = coroutineScope.async {
        try {
            //TODO: do auth
            val credential = AgreementApp.loginCredential
            val requests = KamiApi.retrofitService.getPaymentRequests("{\"password\":\"" + credential.password + "\",\"userName\":\"" + credential.userName + "\"}").await()
            //paymentRequestRepository.deleteAllRequests()
//            paymentRequestRepository.insertRequests(requests)
            val serviceRequests = KamiApi.retrofitService.getServiceRequests("{\"password\":\"" + credential.password + "\",\"userName\":\"" + credential.userName + "\"}").await()
            //serviceRequestRepository.deleteAllRequests()
            serviceRequestRepository.insertRequests(serviceRequests)
        } catch (e: Exception) {
            Log.i(TAG, "updatePaymentRequests", e)
        }
    }

    fun showPaymentRequest(request: Request) {
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
