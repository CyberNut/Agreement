package ru.cybernut.agreement.di

import org.koin.dsl.module
import org.koin.core.qualifier.named
import org.koin.androidx.viewmodel.dsl.viewModel
import ru.cybernut.agreement.db.DeliveryRequest
import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.db.ServiceRequest
import ru.cybernut.agreement.viewmodels.*

val viewModelModule  = module  {

    viewModel(named("payment")) { RequestListViewModel<PaymentRequest>(get(named("payment"))) }

    viewModel(named("service")) { RequestListViewModel<ServiceRequest>(get(named("service"))) }

    viewModel(named("delivery")) { RequestListViewModel<DeliveryRequest>(get(named("delivery"))) }

    viewModel { (request: PaymentRequest) -> PaymentRequestViewModel(request) }

    viewModel { (request: ServiceRequest) -> ServiceRequestViewModel(request) }

    viewModel { (request: DeliveryRequest) -> DeliveryRequestViewModel(request) }

}