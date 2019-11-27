package ru.cybernut.agreement.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json


@Entity(tableName = "payment_requests_table")
data class PaymentRequest constructor(
    @PrimaryKey(autoGenerate = false)
    val uuid: String,
    val number: String,
    val date: String,
    @ColumnInfo(name = "payment_date")
    @Json(name = "payment_date")
    val paymentDate: String,
    val client: String,
    val description: String,
    val currency: String,
    val sum: String,
    val author:String
)
