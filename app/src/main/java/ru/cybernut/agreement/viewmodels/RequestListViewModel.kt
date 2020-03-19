package ru.cybernut.agreement.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import ru.cybernut.agreement.data.Request
import ru.cybernut.agreement.db.AgreementsDatabase

enum class KamiApiStatus  { LOADING, ERROR, DONE }

abstract class RequestListViewModel(application: Application): AndroidViewModel(application) {

//    private val TAG = "RequestListViewModel"

    protected var database: AgreementsDatabase

    private val _navigateToSelectedRequest = MutableLiveData<Request>()
    val navigateToSelectedRequest: LiveData<Request>
        get() = _navigateToSelectedRequest

    private var viewModelJob = Job()

    abstract var request: LiveData<List<Request>>
    protected val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        database = AgreementsDatabase.getDatabase(application)
    }

    abstract fun getRequests(): LiveData<List<out Request>>

    abstract fun updateRequests(): Deferred<Any>
//            = coroutineScope.async {
//        try {
//            //TODO: do auth
//            val credential = AgreementApp.loginCredential
//            val requests = KamiApi.retrofitService.getPaymentRequests("{\"password\":\"" + credential.password + "\",\"userName\":\"" + credential.userName + "\"}").await()
//            //paymentRequestRepository.deleteAllRequests()
////            paymentRequestRepository.insertRequests(requests)
//        } catch (e: Exception) {
//            Log.i(TAG, "updatePaymentRequests", e)
//        }
//    }

    fun showRequest(request: Request) {
        _navigateToSelectedRequest.value = request
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun navigateToSelectedRequestComplete() {
        _navigateToSelectedRequest.value = null
    }
}
