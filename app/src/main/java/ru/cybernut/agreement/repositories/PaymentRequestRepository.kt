package ru.cybernut.agreement.repositories

import androidx.lifecycle.LiveData
import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.db.PaymentRequestDao

class PaymentRequestRepository private constructor(private val paymentRequestDao: PaymentRequestDao) : RequestRepository<PaymentRequest> {

    override fun getRequests() = paymentRequestDao.getRequests()

    override fun getFilteredRequests(filter: String) = paymentRequestDao.getRequestsByFilter(filter)

    override fun getRequestById(requestId: String) = paymentRequestDao.getRequestById(requestId)

    override suspend fun insertRequests(requests: List<PaymentRequest>) {
        //paymentRequestDao.deleteAll()
        paymentRequestDao.insertAll(requests)
    }

    override suspend fun deleteRequest(request: PaymentRequest) = paymentRequestDao.delete(request)

    override suspend fun deleteAllRequests() = paymentRequestDao.deleteAll()

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: PaymentRequestRepository? = null

        fun getInstance(paymentRequestDao: PaymentRequestDao) =
            instance ?: synchronized(this) {
                instance ?: PaymentRequestRepository(paymentRequestDao).also { instance = it }
            }
    }

}