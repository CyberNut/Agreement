package ru.cybernut.agreement.data



class ApprovingRequestList(val loginCredential: LoginCredential) {

    var requestIdList: MutableList<String> = mutableListOf<String>()

    fun addRequestId(id: String) {
        requestIdList.add(id)
    }

}