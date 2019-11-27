package ru.cybernut.agreement.repositories

import ru.cybernut.agreement.db.PaymentRequestDao

class PaymentRequestRepository private constructor(private val paymentRequestDao: PaymentRequestDao) {

    fun getRequests() = paymentRequestDao.getPaymentRequests()

    fun getRequestById(requestId: String) = paymentRequestDao.getPaymentRequestById(requestId)

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: PaymentRequestRepository? = null

        fun getInstance(paymentRequestDao: PaymentRequestDao) =
            instance ?: synchronized(this) {
                instance ?: PaymentRequestRepository(paymentRequestDao).also { instance = it }
            }
    }
}
