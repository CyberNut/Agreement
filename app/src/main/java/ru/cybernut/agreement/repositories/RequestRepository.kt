package ru.cybernut.agreement.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.db.BaseRequestDao
import timber.log.Timber

class RequestRepository<T>(private val fetchFun: suspend (String) -> List<T>, private val dao: BaseRequestDao<T>, private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : Repository<T> {

    override suspend fun getRequests(forceUpdate: Boolean): LiveData<List<T>> = withContext(ioDispatcher) {
        val liveData = MutableLiveData<List<T>>()
        var requests = dao.getRequests(AgreementApp.loginCredential.userName)
        if (forceUpdate) {
            val requestsFromServer = fetchFun(AgreementApp.loginCredential.userName)
            requests = requestsFromServer
            dao.deleteAll(AgreementApp.loginCredential.userName)
            dao.insertAll(requestsFromServer)
        }
        liveData.apply { postValue(requests) }
    }

    override fun getFilteredRequests(filter: String): LiveData<List<T>> = dao.getRequestsByFilter(filter, AgreementApp.loginCredential.userName)

    override fun getRequestById(requestId: String): LiveData<T> = dao.getRequestById(requestId, AgreementApp.loginCredential.userName)

}