package ru.cybernut.agreement.di

import org.koin.dsl.module
import org.koin.core.qualifier.named
import org.koin.androidx.viewmodel.dsl.viewModel
import ru.cybernut.agreement.db.DeliveryRequest
import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.db.ServiceRequest
import ru.cybernut.agreement.viewmodels.*

val viewModelModule  = module  {

//    viewModel { PaymentRequestListViewModel(get()) }
//
//    viewModel { ServiceRequestListViewModel(get()) }
//
//    viewModel { DeliveryRequestListViewModel(get()) }
    viewModel(named("payment")) { ListViewModel<PaymentRequest>(get(named("payment"))) }
    viewModel(named("service")) { ListViewModel<ServiceRequest>(get(named("service"))) }
    viewModel(named("delivery")) { ListViewModel<DeliveryRequest>(get(named("delivery"))) }

    viewModel { (request: PaymentRequest) -> PaymentRequestViewModel(request) }

    viewModel { (request: ServiceRequest) -> ServiceRequestViewModel(request) }

    viewModel { (request: DeliveryRequest) -> DeliveryRequestViewModel(request) }

}