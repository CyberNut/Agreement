package ru.cybernut.agreement.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import ru.cybernut.agreement.utils.waitForValue
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var userDao: UserDao
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
        userDao = db.userDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun getUsers() = runBlocking {
        var user = User("User1")
        var id = 0L
        id = userDao.insert(user)
        user = User( "User2")
        id = userDao.insert(user)
        user = User("User3")
        id = userDao.insert(user)
        val users = userDao.getUsers().waitForValue()
        Assert.assertTrue(users.size > 0)
        //assertEquals(paymentRequests[0].name, questionSet.name)
    }

    @Test
    @Throws(Exception::class)
    fun getUsersById() = runBlocking {
        var user = User(1L, "User1")
        userDao.insert(user)
        user = User(2L, "User2")
        userDao.insert(user)
        val tempUser = userDao.getUserById(2L).waitForValue()
        Assert.assertTrue(tempUser.userName.equals("User2"))
        //assertEquals(paymentRequests[0].name, questionSet.name)
    }

    @Test
    @Throws(Exception::class)
    fun getUsersByName() = runBlocking {
        var id = 0L
        var user = User("User1")
        userDao.insert(user)
        user = User( "User2")
        id = userDao.insert(user)
        user = User("User3")
        userDao.insert(user)
        //val users = userDao.getUsers().waitForValue()
        val tempUser = userDao.getUserByUserName("User2").waitForValue()
        Assert.assertTrue(tempUser.id == id)
    }



}