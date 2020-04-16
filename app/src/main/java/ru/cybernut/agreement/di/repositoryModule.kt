package ru.cybernut.agreement.di

import org.koin.dsl.module
import ru.cybernut.agreement.repositories.DeliveryRequestRepository
import ru.cybernut.agreement.repositories.PaymentRequestRepository
import ru.cybernut.agreement.repositories.ServiceRequestRepository


val repositoryModule = module {

    single { PaymentRequestRepository(get()) }

    single { ServiceRequestRepository(get()) }

    single { DeliveryRequestRepository(get()) }
}
