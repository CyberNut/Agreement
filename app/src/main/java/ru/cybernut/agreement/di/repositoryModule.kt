package ru.cybernut.agreement.di

import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.cybernut.agreement.db.BaseRequestDao
import ru.cybernut.agreement.db.DeliveryRequest
import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.db.ServiceRequest
import ru.cybernut.agreement.network.KamiApi
import ru.cybernut.agreement.repositories.*
import ru.cybernut.agreement.utils.RequestType


val repositoryModule = module {

    single(named("payment")) { RequestRepository<PaymentRequest>(KamiApi.retrofitService::getPaymentRequests, get(
        named("payment")) as BaseRequestDao<PaymentRequest>, RequestType.MONEY, Dispatchers.IO) as Repository<PaymentRequest> }

    single(named("service")) { RequestRepository<ServiceRequest>(KamiApi.retrofitService::getServiceRequests, get(
        named("service")) as BaseRequestDao<ServiceRequest>, RequestType.SERVICE, Dispatchers.IO) as Repository<ServiceRequest> }

    single(named("delivery")) { RequestRepository<DeliveryRequest>(KamiApi.retrofitService::getDeliveryRequests, get(
        named("delivery")) as BaseRequestDao<DeliveryRequest>, RequestType.DELIVERY, Dispatchers.IO) as Repository<DeliveryRequest> }

}
