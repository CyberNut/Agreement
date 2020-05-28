package ru.cybernut.agreement.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.network.KamiApi
import ru.cybernut.agreement.repositories.*
import ru.cybernut.agreement.utils.RequestType


val repositoryModule = module {

    single { PaymentRequestRepository(get()) }

    single { ServiceRequestRepository(get()) }

    single { DeliveryRequestRepository(get()) }

    single(named("payment")) { RequestRepository<PaymentRequest>(KamiApi.retrofitService::getPaymentRequests, get(), RequestType.MONEY) as Repository<PaymentRequest> }
}
