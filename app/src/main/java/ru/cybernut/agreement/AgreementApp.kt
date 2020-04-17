package ru.cybernut.agreement

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.cybernut.agreement.data.LoginCredential
import ru.cybernut.agreement.di.persistenceModule
import ru.cybernut.agreement.di.repositoryModule
import ru.cybernut.agreement.di.viewModelModule

class AgreementApp: Application() {

    companion object {
        lateinit var loginCredential: LoginCredential
        var serverAddress = "http://172.16.0.42"
        var baseName = "kami_ageenko"
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {

            androidLogger()

            androidContext(this@AgreementApp)

            modules(persistenceModule)
            modules(repositoryModule)
            modules(viewModelModule)
        }
    }
}