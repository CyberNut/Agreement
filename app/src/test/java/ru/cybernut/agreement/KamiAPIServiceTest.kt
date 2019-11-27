package ru.cybernut.agreement

import org.junit.Test

import org.junit.Assert.*
import ru.cybernut.agreement.network.KamiApi

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class KamiAPIServiceTest {


    @Test
    fun getRequests() {
        val list = KamiApi.retrofitService.getPaymentRequests("""password":"12345@qw)","userName":"Калашник Ольга Георгиевна"}""").await()
        assertEquals(4, 2 + 2)
    }
}
