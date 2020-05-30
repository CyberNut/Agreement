package ru.cybernut.agreement.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.cybernut.agreement.data.Request
import ru.cybernut.agreement.repositories.Repository
import ru.cybernut.agreement.utils.ApprovalType
import ru.cybernut.agreement.utils.KamiApiStatus
import timber.log.Timber


class RequestViewModel<T: Request>(private val repository: Repository<T>, val request: T): ViewModel() {

    private val _status = MutableLiveData<KamiApiStatus>()

    var paymentRequest = MutableLiveData<T>()

    val status: LiveData<KamiApiStatus>
        get() = _status

    private var _approveResult: MutableLiveData<ApprovalType> = MutableLiveData(ApprovalType.NONE)
    val approveResult: LiveData<ApprovalType>
        get() = _approveResult

    init {
        paymentRequest.value = request
    }

    fun handleRequest(approve: Boolean, comment: String) {
        viewModelScope.launch {
            _approveResult.value = repository.handleRequest(approve, comment, listOf(request.uuid))
        }
        Timber.d("approveResult = " + approveResult.value)
    }

    fun onApproveRequestDone() {
        _approveResult.value = ApprovalType.NONE
    }
}

