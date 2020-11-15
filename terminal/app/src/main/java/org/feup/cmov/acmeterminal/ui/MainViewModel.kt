package org.feup.cmov.acmeterminal.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import org.feup.cmov.acmeterminal.data.DataRepository
import org.feup.cmov.acmeterminal.data.Resource
import org.feup.cmov.acmeterminal.data.api.SubmitOrderResponse

class MainViewModel @ViewModelInject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    suspend fun handleData(data: String): Resource<SubmitOrderResponse> {
        return dataRepository.submitOrder(data)
    }
}