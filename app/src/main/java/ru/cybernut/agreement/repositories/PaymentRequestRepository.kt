package ru.cybernut.agreement.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.db.PaymentRequestDao
import ru.cybernut.agreement.network.KamiApi
import timber.log.Timber

class PaymentRequestRepository(private val paymentRequestDao: PaymentRequestDao) : Repository<PaymentRequest> {

    override fun getRequests() = paymentRequestDao.getRequests(AgreementApp.loginCredential.userName)

    override fun getFilteredRequests(filter: String) = paymentRequestDao.getRequestsByFilter(filter, AgreementApp.loginCredential.userName)

    override fun getRequestById(requestId: String) = paymentRequestDao.getRequestById(requestId, AgreementApp.loginCredential.userName)

    override suspend fun insertRequests(requests: List<PaymentRequest>) {
        val credential = AgreementApp.loginCredential
        requests.forEach { it.userName = credential.userName }
        paymentRequestDao.deleteAll(credential.userName)
        paymentRequestDao.insertAll(requests)
    }

    override suspend fun deleteRequest(request: PaymentRequest) = paymentRequestDao.delete(request)

    override suspend fun deleteAllRequests() = paymentRequestDao.deleteAll(AgreementApp.loginCredential.userName)

    override suspend fun updateRequests() = withContext(Dispatchers.IO) {
        try {
            val credential = AgreementApp.loginCredential
            val requests = KamiApi.retrofitService.getPaymentRequests("{\"password\":\"" + credential.password + "\",\"userName\":\"" + credential.userName + "\"}").await()
            deleteAllRequests()
            insertRequests(requests)
        } catch (e: Exception) {
            Timber.d( "updatePaymentRequests" + e.message)
        }
    }
}