package ru.cybernut.agreement.repositories

import androidx.lifecycle.LiveData
import ru.cybernut.agreement.db.DeliveryRequest
import ru.cybernut.agreement.db.DeliveryRequestDao
import ru.cybernut.agreement.db.ServiceRequest
import ru.cybernut.agreement.db.ServiceRequestDao

class DeliveryRequestRepository private constructor(private val deliveryRequestDao: DeliveryRequestDao) : RequestRepository<DeliveryRequest> {

    override fun getRequests() = deliveryRequestDao.getRequests()

    override fun getRequestById(requestId: String) = deliveryRequestDao.getRequestById(requestId)

    override fun getFilteredRequests(filter: String) = deliveryRequestDao.getRequestsByFilter(filter)


    override suspend fun insertRequests(requests: List<DeliveryRequest>) {
        deliveryRequestDao.deleteAll()
        deliveryRequestDao.insertAll(requests)
    }

    override suspend fun deleteRequest(request: DeliveryRequest) = deliveryRequestDao.delete(request)

    override suspend fun deleteAllRequests() = deliveryRequestDao.deleteAll()

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: DeliveryRequestRepository? = null

        fun getInstance(deliveryRequestDao: DeliveryRequestDao) =
            instance ?: synchronized(this) {
                instance ?: DeliveryRequestRepository(deliveryRequestDao).also { instance = it }
            }
    }
}