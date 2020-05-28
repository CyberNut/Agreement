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

    viewModel { (request: PaymentRequest) -> PaymentRequestViewModel(get(), request) }

    viewModel { (request: ServiceRequest) -> ServiceRequestViewModel(get(), request) }

    viewModel { (request: DeliveryRequest) -> DeliveryRequestViewModel(get(), request) }

    viewModel(named("payment")) { ListViewModel<PaymentRequest>(get()) }

}