package ru.cybernut.agreement.di

import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import ru.cybernut.agreement.R
import ru.cybernut.agreement.db.AgreementsDatabase

val persistenceModule = module {

    single {
        Room
            .databaseBuilder(androidApplication(), AgreementsDatabase::class.java,
                androidApplication().getString(R.string.database_name))
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AgreementsDatabase>().paymentRequestsDao() }

    single { get<AgreementsDatabase>().deliveryRequestsDao() }

    single { get<AgreementsDatabase>().serviceRequestsDao() }
}
