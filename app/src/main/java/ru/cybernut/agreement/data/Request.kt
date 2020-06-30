package ru.cybernut.agreement.data

import android.os.Parcelable

interface Request: Parcelable {
    val uuid: String
    var userName: String
    override fun equals(other: Any?): Boolean
}