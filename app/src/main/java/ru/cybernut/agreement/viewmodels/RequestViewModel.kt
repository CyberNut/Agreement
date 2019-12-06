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

class RequestViewModel(application: Application, val request: PaymentRequest): AndroidViewModel(application) {

    private val TAG = "RequestViewModel"
    private val _status = MutableLiveData<KamiApiStatus>()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    var paymentRequest = MutableLiveData<PaymentRequest>()
    lateinit var paymentRequestRepository: PaymentRequestRepository

    val status: LiveData<KamiApiStatus>
        get() = _status

    private var _confirmed = MutableLiveData<Boolean>(false)
    val confirmed: LiveData<Boolean>
        get() = _confirmed

    private var _needShowToast = MutableLiveData<Boolean>()
    val needShowToast: LiveData<Boolean>
        get() = _needShowToast

    init {
        val database = AgreementsDatabase.getDatabase(application)
        val paymentRequestDao = database.paymentRequestsDao()
        paymentRequestRepository = PaymentRequestRepository.getInstance(paymentRequestDao)
        paymentRequest.value = request
    }

    fun handleRequest(approve: Boolean) {
        //TODO: Обработка согласования
        showToast()
        Log.i(TAG, "Approve = $approve, Request = ${paymentRequest.value}")
    }

    fun onToastShowDone() {
        _needShowToast.value = false
    }

    fun showToast() {
        _needShowToast.value = true
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

