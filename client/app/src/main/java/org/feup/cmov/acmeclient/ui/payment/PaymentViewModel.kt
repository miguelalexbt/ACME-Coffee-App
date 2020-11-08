package org.feup.cmov.acmeclient.ui.payment

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import okio.ByteString
import org.feup.cmov.acmeclient.data.DataRepository

class PaymentViewModel @ViewModelInject constructor(
    dataRepository: DataRepository
) : ViewModel() {

    val orderString: Flow<ByteString> = dataRepository.generateOrderString()

}