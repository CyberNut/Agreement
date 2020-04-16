package ru.cybernut.agreement.data

import java.io.Serializable

interface Request: Serializable {
    val uuid: String
    override fun equals(other: Any?): Boolean
}