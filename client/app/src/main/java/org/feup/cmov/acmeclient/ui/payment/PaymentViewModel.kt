package org.feup.cmov.acmeclient.ui.payment

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import okio.ByteString
import org.feup.cmov.acmeclient.data.DataRepository
import org.feup.cmov.acmeclient.data.Resource

class PaymentViewModel @ViewModelInject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    val orderString: Flow<ByteString> = dataRepository.generateOrderString()

}