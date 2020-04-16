package ru.cybernut.agreement.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.cybernut.agreement.db.ServiceRequest

class ServiceRequestViewModelFactory(
    private val request: ServiceRequest
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ServiceRequestViewModel(request) as T
    }
}
