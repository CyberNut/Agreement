package ru.cybernut.agreement.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import ru.cybernut.agreement.utils.DATABASE_NAME

interface BaseRequestDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(request: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(request: List<T>)

    @Delete
    suspend fun delete(request: T)

    fun getRequests(userName: String): LiveData<List<T>>

    fun getRequestsByFilter(filter: String, userName: String): LiveData<List<T>>

    fun getRequestById(uuid: String, userName: String): LiveData<T>

    suspend fun deleteAll(userName: String)
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
    abstract override suspend fun deleteAll(userName: String)
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
    abstract override suspend fun deleteAll(userName: String)
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
    abstract override suspend fun deleteAll(userName: String)
}



@Database(entities = [PaymentRequest::class, ServiceRequest::class, DeliveryRequest::class], version = 1, exportSchema = false)
abstract class AgreementsDatabase : RoomDatabase() {

    abstract fun paymentRequestsDao(): PaymentRequestDao
    abstract fun serviceRequestsDao(): ServiceRequestDao
    abstract fun deliveryRequestsDao(): DeliveryRequestDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AgreementsDatabase? = null

        fun getDatabase(context: Context): AgreementsDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AgreementsDatabase::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}