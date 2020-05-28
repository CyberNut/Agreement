package ru.cybernut.agreement.repositories

import androidx.lifecycle.LiveData
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.data.ApprovingRequestList

interface Repository<T> {

    suspend fun getRequests(forceUpdate: Boolean = false): LiveData<List<T>>

    suspend fun getFilteredRequests(filter: String): LiveData<List<T>>

    suspend fun getRequestById(requestId: String): LiveData<T>

    suspend fun approveResquest()

    fun getJsonFromRequestIds(requestIds: List<String>): String {
        val approvingRequestList = ApprovingRequestList(AgreementApp.loginCredential)
        requestIds.forEach { approvingRequestList.addRequestId(it) }
        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<ApprovingRequestList> = moshi.adapter(ApprovingRequestList::class.java)
        return jsonAdapter.toJson(approvingRequestList)
    }

}