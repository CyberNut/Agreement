package ru.cybernut.agreement.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import ru.cybernut.agreement.data.Request
import java.io.Serializable

@Entity(tableName = "payment_requests_table")
data class PaymentRequest constructor(
    @PrimaryKey(autoGenerate = false)
    override val uuid: String,
    val number: String,
    val date: String,
    @ColumnInfo(name = "payment_date")
    @Json(name = "payment_date")
    val paymentDate: String,
    val client: String,
    val description: String,
    val currency: String,
    val sum: String,
    val author:String,
    var userName: String = ""
) : Serializable, Request

@Entity(tableName = "service_requests_table")
data class ServiceRequest constructor(
    @PrimaryKey(autoGenerate = false)
    override val uuid: String,
    val number: String,
    val date: String,
    @ColumnInfo(name = "service_dept")
    @Json(name = "service_dept")
    val serviceDept: String,
    val dept: String,
    val client: String,
    val company: String,
    val description: String,
    val sum: String,
    val author:String,
    var userName: String = ""
) : Serializable, Request

@Entity(tableName = "delivery_requests_table")
data class DeliveryRequest constructor(
    @PrimaryKey(autoGenerate = false)
    override val uuid: String,
    val number: String,
    val date: String,
    @ColumnInfo(name = "warehouse_sender")
    @Json(name = "warehouse_sender")
    val warehouseSender: String,
    @ColumnInfo(name = "sender_address")
    @Json(name = "sender_address")
    val senderAddress: String,
    val client: String,
    @ColumnInfo(name = "client_address")
    @Json(name = "client_address")
    val clientAddress: String,
    val company: String,
    val description: String,
    val sum: String,
    val author:String,
    var userName: String = ""
) : Serializable, Request

@Entity(tableName = "users_table")
data class User constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val userName: String
) {
    constructor(userName: String) : this(0L, userName) {
    }
}