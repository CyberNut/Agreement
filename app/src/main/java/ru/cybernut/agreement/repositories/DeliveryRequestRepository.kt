package ru.cybernut.agreement.repositories

import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.db.DeliveryRequest
import ru.cybernut.agreement.db.DeliveryRequestDao

class DeliveryRequestRepository private constructor(private val deliveryRequestDao: DeliveryRequestDao) : RequestRepository<DeliveryRequest> {

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

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: DeliveryRequestRepository? = null

        fun getInstance(deliveryRequestDao: DeliveryRequestDao) =
            instance ?: synchronized(this) {
                instance ?: DeliveryRequestRepository(deliveryRequestDao).also { instance = it }
            }
    }
}