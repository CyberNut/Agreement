package ru.cybernut.agreement.db

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
import ru.cybernut.agreement.utils.waitForValue
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PaymentRequestDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

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
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun getRequests() = runBlocking {
        val paymentRequest = PaymentRequest("1","pay","shop1", "none", "true", "test", "false","100", "sdf", userName = "555")
        paymentRequestDao.insert(paymentRequest)
        val paymentRequests = paymentRequestDao.getRequests("555").waitForValue()
        assertTrue(paymentRequests.size > 0)
        //assertEquals(paymentRequests[0].name, questionSet.name)
    }

    @Test
    @Throws(Exception::class)
    fun getFilteredRequests() = runBlocking {
        val paymentRequest1 = PaymentRequest("1","pay1","shop1", "none", "REIGNMAC", "test1", "false","100", "sdf", userName = "555")
        paymentRequestDao.insert(paymentRequest1)
        val paymentRequest2 = PaymentRequest("2","pay2","shop2", "none", "TWT GLOBAL ENTERPRISE", "test2", "false","102", "sdf", userName = "555")
        paymentRequestDao.insert(paymentRequest2)
        val paymentRequest3 = PaymentRequest("3","pay3","shop3", "none", "TWT GLOBAL ENTERPRISE", "test3", "false","103", "ssdff", userName = "666")
        paymentRequestDao.insert(paymentRequest3)

        val paymentRequests = paymentRequestDao.getRequestsByFilter("mac", "555").waitForValue()
        assertTrue(paymentRequests.size == 1)
        //assertEquals(paymentRequests[0].name, questionSet.name)
    }


    @Test
    @Throws(Exception::class)
    fun deleteAll() = runBlocking {
        val paymentRequest = PaymentRequest("1","pay","shop1", "none", "true", "test", "false","100", "sdf", "555")
        paymentRequestDao.insert(paymentRequest)
        var paymentRequests = paymentRequestDao.getRequests("555").waitForValue()
        assertTrue(paymentRequests.size > 0)
        paymentRequestDao.deleteAll("555")
        paymentRequests = paymentRequestDao.getRequests("555").waitForValue()
        assertTrue(paymentRequests.isEmpty())
    }
}