package ru.cybernut.agreement.repositories

import androidx.lifecycle.LiveData

interface Repository<T> {

    suspend fun getRequests(forceUpdate: Boolean = false): LiveData<List<T>>

    fun getFilteredRequests(filter: String): LiveData<List<T>>

    fun getRequestById(requestId: String): LiveData<T>

    fun approveResquest()
}