package ru.cybernut.agreement.utils

const val DATABASE_NAME = "agreements-db"
const val MIN_SEARCH_QUERY_LENGHT = 3
enum class RequestType {
    MONEY, SERVICE, DELIVERY
}

enum class ApprovalType {
    NONE, APPROVE, DECLINE, ERROR
}

enum class KamiApiStatus  { LOADING, ERROR, DONE }