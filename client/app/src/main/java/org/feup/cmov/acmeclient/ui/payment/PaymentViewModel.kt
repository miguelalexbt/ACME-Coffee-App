package org.feup.cmov.acmeclient.ui.payment

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okio.ByteString
import org.feup.cmov.acmeclient.data.DataRepository

class PaymentViewModel @ViewModelInject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _isNfcActive = MutableLiveData<Boolean>()
    val isNfcActive: LiveData<Boolean> = _isNfcActive.distinctUntilChanged()

    val orderString: Flow<ByteString> = dataRepository.generateOrderString()

    fun setNfcStatus(active: Boolean) {
        _isNfcActive.value = active
    }

    fun clearOrder() {
        viewModelScope.launch {
            dataRepository.clearOrder()
        }
    }
}