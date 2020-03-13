package ru.cybernut.agreement

import android.app.Application
import ru.cybernut.agreement.data.LoginCredential

class AgreementApp: Application() {

    companion object {
        lateinit var loginCredential: LoginCredential
        var userId: Long = 0L
    }
}