package ru.cybernut.agreement.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import ru.cybernut.agreement.db.DeliveryRequest
import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.db.ServiceRequest

private const val BASE_URL = "http://srv-app1c3.kami.local/kami/hs/Approval/"

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

/**
 * A public interface that exposes the [getProperties] method
 */
interface KamiAPIService {

    @POST("login")
    fun doLogin(@Body loginString: String?): Call<Void>

    @POST("getList")
    fun getPaymentRequests(@Body loginCredential: String): Deferred<List<PaymentRequest>>

    @POST("getListService")
    fun getServiceRequests(@Body loginCredential: String): Deferred<List<ServiceRequest>>

    @POST("getListDelivery")
    fun getDeliveryRequests(@Body loginCredential: String): Deferred<List<DeliveryRequest>>

    @POST("approve/{request_type}/{type}/{comment}")
    fun approveRequests(@Path("request_type") requestType: String,
                        @Path("type") type: Boolean,
                        @Path("comment") comment: String?,
                        @Body approveBody: String?
    ): Call<Void>

}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object KamiApi {
    val retrofitService : KamiAPIService by lazy { retrofit.create(KamiAPIService::class.java) }
}
