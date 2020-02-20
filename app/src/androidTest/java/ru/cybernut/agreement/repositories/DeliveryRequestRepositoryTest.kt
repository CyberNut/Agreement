package ru.cybernut.agreement.repositories

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.cybernut.agreement.db.AgreementsDatabase
import ru.cybernut.agreement.db.DeliveryRequest
import ru.cybernut.agreement.db.DeliveryRequestDao
import ru.cybernut.agreement.utils.waitForValue
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class DeliveryRequestRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var deliveryRequestRepository: DeliveryRequestRepository
    private lateinit var deliveryRequestDao: DeliveryRequestDao
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
        deliveryRequestDao = db.deliveryRequestsDao()
        deliveryRequestRepository = DeliveryRequestRepository.getInstance(deliveryRequestDao)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun getRequests() = runBlocking {
        val deliveryRequest = DeliveryRequest("1","pay","shop1", "none", "true", "test", "false","100", "sdf","1", "2")
        deliveryRequestRepository.insertRequests(listOf(deliveryRequest))
        val deliveryRequests = deliveryRequestRepository.getRequests().waitForValue()
        //val paymentRequests = paymentRequestDao.getRequests().waitForValue()
        assertTrue(deliveryRequests.size > 0)
    }

    @Test
    fun getRequestById() = runBlocking {
        val deliveryRequest = DeliveryRequest("1","pay","shop1", "none", "true", "test", "false","100", "sdf","1", "2")
        deliveryRequestRepository.insertRequests(listOf(deliveryRequest))
        val deliveryRequests = deliveryRequestRepository.getRequestById("1").waitForValue()
        assertTrue(deliveryRequests.company.equals("100"))
    }

//    @Test
//    fun insertRequests() {
//    }
//
    @Test
    fun deleteRequest() = runBlocking {
        val deliveryRequest = DeliveryRequest("1","pay","shop1", "none", "true", "test", "false","100", "sdf","1", "2")
        deliveryRequestRepository.insertRequests(listOf(deliveryRequest))
        deliveryRequestRepository.deleteRequest(deliveryRequest)
        val deliveryRequests = deliveryRequestRepository.getRequests().waitForValue()
        assertTrue(deliveryRequests.isEmpty())
    }

//    @Test
//    fun deleteAllRequests() {
//    }
}