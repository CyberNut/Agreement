package ru.cybernut.agreement.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.cybernut.agreement.db.PaymentRequest

class RequestViewModelFactory(
    private val application: Application,
    private val request: PaymentRequest
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RequestViewModel(application, request) as T
    }
}
