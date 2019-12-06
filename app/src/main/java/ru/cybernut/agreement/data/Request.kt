package ru.cybernut.agreement.data

import java.io.Serializable

//abstract class Request(open val uuid: String) {
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (other !is Request) return false
//
//        if (uuid != other.uuid) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        return uuid.hashCode()
//    }
//}


interface Request: Serializable {
    val uuid: String
    override fun equals(other: Any?): Boolean
}