package ru.cybernut.agreement.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.cybernut.agreement.utils.RequestType

class RequestListViewModelFactory(
    private val application: Application,
    private val requestType: RequestType
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        when (requestType) {
            RequestType.MONEY -> {
                return PaymentRequestListViewModel(application) as T
            }
            RequestType.SERVICE -> {
                return PaymentRequestListViewModel(application) as T
            }
            RequestType.DELIVERY -> {
                return PaymentRequestListViewModel(application) as T
            }
        }
    }
}
