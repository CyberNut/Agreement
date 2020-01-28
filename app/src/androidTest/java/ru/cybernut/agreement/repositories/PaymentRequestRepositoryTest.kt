package ru.cybernut.agreement.repositories

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import ru.cybernut.agreement.db.AgreementsDatabase
import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.db.PaymentRequestDao
import ru.cybernut.fivesecondsgame.waitForValue
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class PaymentRequestRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var paymentRequestRepository: PaymentRequestRepository
    private lateinit var paymentRequestDao: PaymentRequestDao
    private lateinit var db: AgreementsDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, AgreementsDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        paymentRequestDao = db.paymentRequestsDao()
        paymentRequestRepository = PaymentRequestRepository.getInstance(paymentRequestDao)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun getRequests() = runBlocking {
        val paymentRequest = PaymentRequest("1","pay","shop1", "none", "true", "test", "false","100", "sdf")
        paymentRequestRepository.insertRequests(listOf(paymentRequest))
        val paymentRequests = paymentRequestRepository.getRequests().waitForValue()
        //val paymentRequests = paymentRequestDao.getRequests().waitForValue()
        assertTrue(paymentRequests.size > 0)
    }

    @Test
    fun getRequestById() = runBlocking {
        val paymentRequest = PaymentRequest("1","pay","shop1", "none", "true", "test", "false","100", "sdf")
        paymentRequestRepository.insertRequests(listOf(paymentRequest))
        val paymentRequests = paymentRequestRepository.getRequestById("1").waitForValue()
        assertTrue(paymentRequests.sum.equals("100"))
    }

//    @Test
//    fun insertRequests() {
//    }
//
    @Test
    fun deleteRequest() = runBlocking {
        val paymentRequest = PaymentRequest("1","pay","shop1", "none", "true", "test", "false","100", "sdf")
        paymentRequestRepository.insertRequests(listOf(paymentRequest))
        paymentRequestRepository.deleteRequest(paymentRequest)
        val paymentRequests = paymentRequestRepository.getRequests().waitForValue()
        assertTrue(paymentRequests.isEmpty())
    }

//    @Test
//    fun deleteAllRequests() {
//    }
}