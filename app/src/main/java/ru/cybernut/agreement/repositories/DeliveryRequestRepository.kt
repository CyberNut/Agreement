package ru.cybernut.agreement.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.db.DeliveryRequest
import ru.cybernut.agreement.db.DeliveryRequestDao
import ru.cybernut.agreement.network.KamiApi
import timber.log.Timber

class DeliveryRequestRepository(private val deliveryRequestDao: DeliveryRequestDao) : Repository<DeliveryRequest> {

    override fun getRequests() = deliveryRequestDao.getRequests(AgreementApp.loginCredential.userName)

    override fun getRequestById(requestId: String) = deliveryRequestDao.getRequestById(requestId, AgreementApp.loginCredential.userName)

    override fun getFilteredRequests(filter: String) = deliveryRequestDao.getRequestsByFilter(filter, AgreementApp.loginCredential.userName)

    override suspend fun insertRequests(requests: List<DeliveryRequest>) {
        val credential = AgreementApp.loginCredential
        requests.forEach { it.userName = credential.userName }
        deliveryRequestDao.deleteAll(credential.userName)
        deliveryRequestDao.insertAll(requests)
    }

    override suspend fun deleteRequest(request: DeliveryRequest) = deliveryRequestDao.delete(request)

    override suspend fun deleteAllRequests() = deliveryRequestDao.deleteAll(AgreementApp.loginCredential.userName)

    override suspend fun updateRequests() = withContext(Dispatchers.IO) {
        try {
            val credential = AgreementApp.loginCredential
            val requests = KamiApi.retrofitService.getDeliveryRequests("{\"password\":\"" + credential.password + "\",\"userName\":\"" + credential.userName + "\"}").await()
            deleteAllRequests()
            insertRequests(requests)
        } catch (e: Exception) {
            Timber.d( "updatePaymentRequests" + e.message)
        }
    }
}