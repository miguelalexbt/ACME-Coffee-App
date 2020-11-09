package org.feup.cmov.acmeclient.ui.payment

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import kotlinx.coroutines.flow.Flow
import okio.ByteString
import org.feup.cmov.acmeclient.data.DataRepository

class PaymentViewModel @ViewModelInject constructor(
    dataRepository: DataRepository
) : ViewModel() {

    private val _isNfcActive = MutableLiveData<Boolean>()
    val isNfcActive: LiveData<Boolean> = _isNfcActive.distinctUntilChanged()

    val orderString: Flow<ByteString> = dataRepository.generateOrderString()

    fun setNfcStatus(active: Boolean) {
        _isNfcActive.value = active
    }

}