package ru.cybernut.agreement.di

import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.cybernut.agreement.R
import ru.cybernut.agreement.db.*

val persistenceModule = module {

    single {
        Room
            .databaseBuilder(androidApplication(), AgreementsDatabase::class.java,
                androidApplication().getString(R.string.database_name))
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    single(named("payment")) { get<AgreementsDatabase>().paymentRequestsDao() as BaseRequestDao<PaymentRequest> }

    single(named("service")) { get<AgreementsDatabase>().serviceRequestsDao() as BaseRequestDao<ServiceRequest>}

    single(named("delivery")) { get<AgreementsDatabase>().deliveryRequestsDao() as BaseRequestDao<DeliveryRequest>}
}
