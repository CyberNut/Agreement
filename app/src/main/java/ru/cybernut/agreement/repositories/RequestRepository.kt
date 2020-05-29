package ru.cybernut.agreement.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.data.ApprovalType
import ru.cybernut.agreement.data.Request
import ru.cybernut.agreement.db.BaseRequestDao
import ru.cybernut.agreement.network.KamiApi
import ru.cybernut.agreement.utils.RequestType
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RequestRepository<T: Request>(private val fetchFun: suspend (String) -> List<T>,
                           private val dao: BaseRequestDao<T>,
                           private val requestType: RequestType,
                           private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : Repository<T> {

    override fun getRequests(): LiveData<List<T>> {
        Timber.d("From getRequests()")
        val temp = dao.getRequests(AgreementApp.loginCredential.userName)
        return temp
    }

    fun insertRequests(requests: List<T>) {
        val credential = AgreementApp.loginCredential
        requests.forEach { it.userName = credential.userName }
        dao.deleteAll(credential.userName)
        dao.insertAll(requests)
    }

    override suspend fun fetchRequests() = withContext(ioDispatcher) {
        val credential = AgreementApp.loginCredential
        val requestsFromServer = fetchFun("{\"password\":\"" + credential.password + "\",\"userName\":\"" + credential.userName + "\"}")
        insertRequests(requestsFromServer)
    }

    override fun getFilteredRequests(filter: String): LiveData<List<T>> = dao.getRequestsByFilter(filter, AgreementApp.loginCredential.userName)

    override fun getRequestById(requestId: String): LiveData<T> = dao.getRequestById(requestId, AgreementApp.loginCredential.userName)

    suspend fun handleRequest(approve: Boolean, comment: String, requestIds: List<String>): ApprovalType {
        var approveResult: ApprovalType
        val json = getJsonFromRequestIds(requestIds)
        val commentary = comment.trim() + " (Mobile)"
        Timber.d("Before starting approve request")
        try {
            approveResult = sendApproveRequest(approve, commentary, json)
        } catch (e: Exception) {
            Timber.d("KamiApi.retrofitService.approveRequests failure " + e.message)
            approveResult = ApprovalType.ERROR
        }
        Timber.d("After starting approve request")
        return approveResult
    }

    private suspend fun sendApproveRequest(approve: Boolean, comment: String, json: String): ApprovalType {
        return suspendCoroutine { cont ->
            KamiApi.retrofitService.approveRequests(
                requestType.toString(),
                approve,
                comment,
                json
            )
                .enqueue(object : Callback<Void> {
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Timber.d("ERROR Approve = $approve")
                        cont.resume(ApprovalType.ERROR)
                    }

                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Timber.d("SUCCESS Approve = $approve")
                        return cont.resume(if (approve) ApprovalType.APPROVE else ApprovalType.DECLINE)
                    }
                })
        }
    }

    override suspend fun approveResquest() {
        TODO("Not yet implemented")
    }

}