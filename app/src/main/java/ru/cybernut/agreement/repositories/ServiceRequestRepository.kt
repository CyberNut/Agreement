package ru.cybernut.agreement.repositories

import ru.cybernut.agreement.db.ServiceRequest
import ru.cybernut.agreement.db.ServiceRequestDao

class ServiceRequestRepository private constructor(private val serviceRequestDao: ServiceRequestDao) : RequestRepository<ServiceRequest> {

    override fun getRequests() = serviceRequestDao.getRequests()

    override fun getFilteredRequests(filter: String) = serviceRequestDao.getRequestsByFilter(filter)

    override fun getRequestById(requestId: String) = serviceRequestDao.getRequestById(requestId)

    override suspend fun insertRequests(requests: List<ServiceRequest>) {
        serviceRequestDao.deleteAll()
        serviceRequestDao.insertAll(requests)
    }

    override suspend fun deleteRequest(request: ServiceRequest) = serviceRequestDao.delete(request)

    override suspend fun deleteAllRequests() = serviceRequestDao.deleteAll()

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: ServiceRequestRepository? = null

        fun getInstance(serviceRequestDao: ServiceRequestDao) =
            instance ?: synchronized(this) {
                instance ?: ServiceRequestRepository(serviceRequestDao).also { instance = it }
            }
    }
}