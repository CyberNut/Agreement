package ru.cybernut.agreement.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

interface RequestRepository<T> {

    fun getRequests(): LiveData<List<T>>

    fun getFilteredRequests(filter: String): LiveData<List<T>>

    fun getRequestById(requestId: String): LiveData<T>

    suspend fun insertRequests(requests: List<T>)

    suspend fun deleteRequest(request: T)

    suspend fun deleteAllRequests()

}