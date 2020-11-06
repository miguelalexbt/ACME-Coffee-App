package org.feup.cmov.acmeterminal.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.feup.cmov.acmeterminal.data.DataRepository

class MainViewModel @ViewModelInject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    fun handleData(data: String?) {
        viewModelScope.launch {
            data ?: return@launch
            println("GOT $data")

            dataRepository.submitOrder(data)
        }
    }
}