package ru.cybernut.agreement.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.utils.RequestType

class RequestListViewModelFactory(
    private val application: Application,
    private val requestType: RequestType
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RequestListViewModel(application, requestType) as T
    }
}
