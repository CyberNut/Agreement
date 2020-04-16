package ru.cybernut.agreement.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.cybernut.agreement.db.DeliveryRequest

class DeliveryRequestViewModelFactory(
    private val request: DeliveryRequest
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DeliveryRequestViewModel(request) as T
    }
}
