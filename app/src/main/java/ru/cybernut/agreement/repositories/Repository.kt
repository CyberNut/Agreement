package ru.cybernut.agreement.repositories

import androidx.lifecycle.LiveData
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.data.ApprovingRequestList
import ru.cybernut.agreement.data.Request
import ru.cybernut.agreement.utils.ApprovalType

interface Repository<T: Request> {

    fun getRequests(): LiveData<List<T>>

    fun getFilteredRequests(filter: String): LiveData<List<T>>

    fun getRequestById(requestId: String): LiveData<T>

    fun deleteRequestList(requestIds: List<String>)

    suspend fun fetchRequests()

    suspend fun handleRequest(approve: Boolean, comment: String, requestIds: List<String>): ApprovalType

    fun getJsonFromRequestIds(requestIds: List<String>): String {
        val approvingRequestList = ApprovingRequestList(AgreementApp.loginCredential)
        requestIds.forEach { approvingRequestList.addRequestId(it) }
        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<ApprovingRequestList> = moshi.adapter(ApprovingRequestList::class.java)
        return jsonAdapter.toJson(approvingRequestList)
    }

}