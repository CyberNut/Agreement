package ru.cybernut.agreement.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.cybernut.agreement.db.PaymentRequest

class PaymentRequestViewModelFactory(
    private val request: PaymentRequest
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PaymentRequestViewModel(request) as T
    }
}
