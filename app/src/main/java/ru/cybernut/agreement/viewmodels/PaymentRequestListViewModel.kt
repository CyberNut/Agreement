package ru.cybernut.agreement.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.async
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.data.Request
import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.network.KamiApi
import ru.cybernut.agreement.repositories.PaymentRequestRepository


class PaymentRequestListViewModel(application: Application) : RequestListViewModel(application) {

    private val TAG = "PaymentRequestListViewModel"
    private var paymentRequestRepository: PaymentRequestRepository

    private var _paymentRequests : LiveData<List<PaymentRequest>>
    val paymentRequests: LiveData<List<PaymentRequest>>
        get() = _paymentRequests

    //private var paymentRequests:LiveData<List<PaymentRequest>>
    //val requests: LiveData<List<PaymentRequest>> = getRequests()
//    override var request: LiveData<List<Request>>
//        get() {
//            val mutableLiveData = MutableLiveData<List<Request>>()
//            val tempList: ArrayList<Request> = arrayListOf()
//            val sourceList = _paymentRequests.value
//            if(sourceList?.isNotEmpty()!!) {
//                for (pr:PaymentRequest in sourceList) {
//                    tempList.add(pr)
//                }
//                mutableLiveData.value = tempList
//
//            }
//            return mutableLiveData
//        }

    init {
        val paymentRequestDao = database.paymentRequestsDao()
        paymentRequestRepository = PaymentRequestRepository.getInstance(paymentRequestDao)
        var tpaymentRequests = paymentRequestRepository.getRequests()
        _paymentRequests = tpaymentRequests
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

    override var request: LiveData<List<Request>>
        get() {
            val mutableLiveData = MutableLiveData<List<Request>>()
            val tempList: ArrayList<Request> = arrayListOf()
            val sourceList = _paymentRequests.value
            if(sourceList?.isNotEmpty()!!) {
                for (pr:PaymentRequest in sourceList) {
                    tempList.add(pr)
                }
                mutableLiveData.value = tempList
            }
            return mutableLiveData
        }
        set(value) {}

//            override fun getRequests(): LiveData<List<Request>> {
//                val mutableLiveData = MutableLiveData<List<Request>>()
//                val tempList: ArrayList<Request> = arrayListOf()
//                val sourceList = _paymentRequests.value
//                if(sourceList?.isNotEmpty()!!) {
//                    for (pr:PaymentRequest in sourceList) {
//                        tempList.add(pr)
//                    }
//                    mutableLiveData.value = tempList
//                }
//                return mutableLiveData
//            }
//        }
}
