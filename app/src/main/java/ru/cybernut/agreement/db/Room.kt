package ru.cybernut.agreement.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import ru.cybernut.agreement.utils.DATABASE_NAME

@Dao
interface PaymentRequestDao {
    @Query("select * from payment_requests_table")
    fun getPaymentRequests(): LiveData<List<PaymentRequest>>

    @Query("select * from payment_requests_table where uuid = :uuid")
    fun getPaymentRequestById(uuid: String): LiveData<PaymentRequest>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(paymentRequest: PaymentRequest)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg paymentRequest: PaymentRequest)

    @Query("DELETE FROM payment_requests_table")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(paymentRequest: PaymentRequest)
}

@Database(entities = [PaymentRequest::class], version = 1, exportSchema = false)
abstract class AgreementsDatabase : RoomDatabase() {

    abstract fun paymentRequestsDao(): PaymentRequestDao

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