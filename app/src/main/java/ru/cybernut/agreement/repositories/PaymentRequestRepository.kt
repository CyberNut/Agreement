package ru.cybernut.agreement.repositories

import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.db.PaymentRequestDao

class PaymentRequestRepository private constructor(private val paymentRequestDao: PaymentRequestDao) {

    fun getRequests() = paymentRequestDao.getPaymentRequests()

    fun getRequestById(requestId: String) = paymentRequestDao.getPaymentRequestById(requestId)

    suspend fun insertRequests(requests: List<PaymentRequest>) {
        paymentRequestDao.deleteAll()
        paymentRequestDao.insertAll(requests)
    }

    suspend fun deleteRequest(request: PaymentRequest) = paymentRequestDao.delete(request)

    suspend fun deleteAllRequests() = paymentRequestDao.deleteAll()

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: PaymentRequestRepository? = null

        fun getInstance(paymentRequestDao: PaymentRequestDao) =
            instance ?: synchronized(this) {
                instance ?: PaymentRequestRepository(paymentRequestDao).also { instance = it }
            }
    }
}
