package ru.cybernut.agreement.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.db.ServiceRequest
import ru.cybernut.agreement.db.ServiceRequestDao
import ru.cybernut.agreement.network.KamiApi
import timber.log.Timber

class ServiceRequestRepository(private val serviceRequestDao: ServiceRequestDao) : Repository<ServiceRequest> {

    override fun getRequests() = serviceRequestDao.getRequests(AgreementApp.loginCredential.userName)

    override fun getFilteredRequests(filter: String) = serviceRequestDao.getRequestsByFilter(filter, AgreementApp.loginCredential.userName)

    override fun getRequestById(requestId: String) = serviceRequestDao.getRequestById(requestId, AgreementApp.loginCredential.userName)

    override suspend fun insertRequests(requests: List<ServiceRequest>) {
        val credential = AgreementApp.loginCredential
        requests.forEach { it.userName = credential.userName }
        serviceRequestDao.deleteAll(credential.userName)
        serviceRequestDao.insertAll(requests)
    }

    override suspend fun deleteRequest(request: ServiceRequest) = serviceRequestDao.delete(request)

    override suspend fun deleteAllRequests() = serviceRequestDao.deleteAll(AgreementApp.loginCredential.userName)

    override suspend fun updateRequests() = withContext(Dispatchers.IO) {
        try {
            val credential = AgreementApp.loginCredential
            val requests = KamiApi.retrofitService.getServiceRequests("{\"password\":\"" + credential.password + "\",\"userName\":\"" + credential.userName + "\"}").await()
            deleteAllRequests()
            insertRequests(requests)
        } catch (e: Exception) {
            Timber.d( "updatePaymentRequests" + e.message)
        }
    }
}