package ru.cybernut.agreement.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BaseRequestDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(request: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(request: List<T>)

    @Delete
    fun delete(request: T)

    fun deleteRequestList(requests: List<String>, userName: String)

    fun getRequests(userName: String): LiveData<List<T>>

    fun getRequestsByFilter(filter: String, userName: String): LiveData<List<T>>

    fun getRequestById(uuid: String, userName: String): LiveData<T>

    fun deleteAll(userName: String)
}

@Dao
abstract class PaymentRequestDao: BaseRequestDao<PaymentRequest> {
    @Query("select * from payment_requests_table where userName = :userName")
    abstract override fun getRequests(userName: String): LiveData<List<PaymentRequest>>

    @Query("select * from payment_requests_table where userName = :userName AND (number LIKE '%' || :filter  || '%' OR client LIKE '%' || :filter  || '%' OR payment_date LIKE '%' || :filter  || '%' OR date LIKE '%' || :filter  || '%' OR sum LIKE '%' || :filter  || '%' OR author LIKE '%' || :filter  || '%' OR description LIKE '%' || :filter  || '%')")
    abstract override fun getRequestsByFilter(filter: String, userName: String): LiveData<List<PaymentRequest>>

    @Query("select * from payment_requests_table where userName = :userName AND uuid = :uuid")
    abstract override fun getRequestById(uuid: String, userName: String): LiveData<PaymentRequest>

    @Query("DELETE FROM payment_requests_table where userName = :userName")
    abstract override fun deleteAll(userName: String)

    @Query("DELETE FROM payment_requests_table where userName = :userName AND uuid IN (:requests)")
    abstract override fun deleteRequestList(requests: List<String>, userName: String)
}

@Dao
abstract class ServiceRequestDao: BaseRequestDao<ServiceRequest> {
    @Query("select * from service_requests_table where userName = :userName")
    abstract override fun getRequests(userName: String): LiveData<List<ServiceRequest>>

    @Query("select * from service_requests_table where userName = :userName AND (number LIKE '%' || :filter  || '%' OR sum LIKE '%' || :filter  || '%' OR author LIKE '%' || :filter  || '%' OR date LIKE '%' || :filter  || '%' OR service_dept LIKE '%' || :filter  || '%' OR dept LIKE '%' || :filter  || '%' OR client LIKE '%' || :filter  || '%' OR description LIKE '%' || :filter  || '%')")
    abstract override fun getRequestsByFilter(filter: String, userName: String): LiveData<List<ServiceRequest>>

    @Query("select * from service_requests_table where userName = :userName AND uuid = :uuid")
    abstract override fun getRequestById(uuid: String, userName: String): LiveData<ServiceRequest>

    @Query("DELETE FROM service_requests_table where userName = :userName")
    abstract override fun deleteAll(userName: String)

    @Query("DELETE FROM service_requests_table where userName = :userName AND uuid IN (:requests)")
    abstract override fun deleteRequestList(requests: List<String>, userName: String)
}

@Dao
abstract class DeliveryRequestDao: BaseRequestDao<DeliveryRequest> {
    @Query("select * from delivery_requests_table where userName = :userName")
    abstract override fun getRequests(userName: String): LiveData<List<DeliveryRequest>>

    @Query("select * from delivery_requests_table where userName = :userName AND (number LIKE '%' || :filter  || '%' OR client LIKE '%' || :filter  || '%' OR sum LIKE '%' || :filter  || '%' OR author LIKE '%' || :filter  || '%' OR date LIKE '%' || :filter  || '%' OR warehouse_sender LIKE '%' || :filter  || '%' OR sender_address LIKE '%' || :filter  || '%' OR client_address LIKE '%' || :filter  || '%' OR client LIKE '%' || :filter  || '%' OR description LIKE '%' || :filter  || '%')")
    abstract override fun getRequestsByFilter(filter: String, userName: String): LiveData<List<DeliveryRequest>>

    @Query("select * from delivery_requests_table where userName = :userName AND uuid = :uuid")
    abstract override fun getRequestById(uuid: String, userName: String): LiveData<DeliveryRequest>

    @Query("DELETE FROM delivery_requests_table where userName = :userName")
    abstract override fun deleteAll(userName: String)

    @Query("DELETE FROM delivery_requests_table where userName = :userName AND uuid IN (:requests)")
    abstract override fun deleteRequestList(requests: List<String>, userName: String)
}

@Database(entities = [PaymentRequest::class, ServiceRequest::class, DeliveryRequest::class], version = 1, exportSchema = false)
abstract class AgreementsDatabase : RoomDatabase() {

    abstract fun paymentRequestsDao(): PaymentRequestDao
    abstract fun serviceRequestsDao(): ServiceRequestDao
    abstract fun deliveryRequestsDao(): DeliveryRequestDao
}