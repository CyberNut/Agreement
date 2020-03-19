package ru.cybernut.agreement.repositories

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.cybernut.agreement.db.AgreementsDatabase
import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.utils.waitForValue
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class PaymentRequestRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var paymentRequestRepository: PaymentRequestRepository
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
        val paymentRequestDao = db.paymentRequestsDao()
        paymentRequestRepository = PaymentRequestRepository.getInstance(paymentRequestDao)
    }

//    @After
//    @Throws(IOException::class)
//    fun closeDb() {
//        db.close()
//    }

    @Test
    @Throws(IOException::class)
    fun deleteRequest() = runBlocking {
        val paymentRequest = PaymentRequest("1","pay","shop1", "none", "true", "test", "false","100", "sdf")
        paymentRequestRepository.deleteAllRequests()
        paymentRequestRepository.insertRequests(listOf(paymentRequest))
        paymentRequestRepository.deleteRequest(paymentRequest)
        val paymentRequests = paymentRequestRepository.getRequests().waitForValue()
        assertTrue(paymentRequests.isEmpty())
    }


    @Test
    @Throws(IOException::class)
    fun getRequests() = runBlocking {
        val paymentRequest1 = PaymentRequest("1","pay1","shop1", "none", "true", "test", "false","101", "sdf")
        val paymentRequest2 = PaymentRequest("2","pay2","shop2", "none", "true", "test", "false","102", "sdf")
        val paymentRequest3 = PaymentRequest("3","pay3","shop3", "none", "true", "test", "false","103", "sdf")
        paymentRequestRepository.deleteAllRequests()
        paymentRequestRepository.insertRequests(listOf(paymentRequest1, paymentRequest2, paymentRequest3))

        val paymentRequests = paymentRequestRepository.getRequests().waitForValue()
        assertTrue(paymentRequests.size == 3)
    }


    @Test
    fun getFilteredRequests() = runBlocking {
        val paymentRequest1 = PaymentRequest("1","pay1","shop1", "none", "REIGNMAC", "test1", "false","100", "sdf")
        val paymentRequest2 = PaymentRequest("2","pay2","shop2", "none", "TWT GLOBAL ENTERPRISE", "test2", "false","102", "sdf")
        val paymentRequest3 = PaymentRequest("3","pay3","shop3", "none", "TWT GLOBAL ENTERPRISE", "test3", "false","103", "ssdff")
        paymentRequestRepository.deleteAllRequests()
        paymentRequestRepository.insertRequests(listOf(paymentRequest1, paymentRequest2, paymentRequest3))
        val paymentRequests = paymentRequestRepository.getFilteredRequests("OBAL").waitForValue()
        //val paymentRequests = paymentRequestDao.getRequests().waitForValue()
        assertTrue(paymentRequests.size == 2)
    }


    @Test
    @Throws(IOException::class)
    fun getRequestById() = runBlocking {
        val paymentRequest = PaymentRequest("2","pay","shop1", "none", "true", "test", "false","100", "sdf")
        paymentRequestRepository.deleteAllRequests()
        paymentRequestRepository.insertRequests(listOf(paymentRequest))
        val paymentRequests = paymentRequestRepository.getRequestById("2").waitForValue()
        assertTrue(paymentRequests.sum.equals("100"))
    }


//    @Test
//    fun deleteAllRequests() {
//    }
}