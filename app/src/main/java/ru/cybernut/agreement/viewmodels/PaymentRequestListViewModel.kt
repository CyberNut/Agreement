package ru.cybernut.agreement.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import kotlinx.coroutines.async
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.data.Request
import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.network.KamiApi
import ru.cybernut.agreement.repositories.PaymentRequestRepository


class PaymentRequestListViewModel(application: Application) : RequestListViewModel(application) {

    private val TAG = "PaymentRequestListViewModel"
    private lateinit var paymentRequestRepository: PaymentRequestRepository

    private lateinit var _requests : MutableLiveData<List<Request>>
    private var paymentRequest:LiveData<List<PaymentRequest>>
    //val requests: LiveData<List<PaymentRequest>> = getRequests()

    init {
        val paymentRequestDao = database.paymentRequestsDao()
        paymentRequestRepository = PaymentRequestRepository.getInstance(paymentRequestDao)
        paymentRequest = paymentRequestRepository.getRequests()
    }

    @SuppressLint("LongLogTag")
    override fun updateRequests() = coroutineScope.async {
        try {
            val credential = AgreementApp.loginCredential
            val requests = KamiApi.retrofitService.getPaymentRequests("{\"password\":\"" + credential.password + "\",\"userName\":\"" + credential.userName + "\"}").await()
            paymentRequestRepository.deleteAllRequests()
            paymentRequestRepository.insertRequests(requests)
        } catch (e: Exception) {
            Log.i(TAG, "updatePaymentRequests", e)
        }
    }

    override fun getRequests(): LiveData<List<Request>> {
        val temp = paymentRequest.value
        var res = ArrayList<Request>()
        if(temp!= null) {
            for (c in temp) {
                res.add(c)
            }
        }
        _requests.value = res
        return _requests
    }
}
