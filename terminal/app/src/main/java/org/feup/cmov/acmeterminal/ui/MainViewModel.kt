package org.feup.cmov.acmeterminal.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.feup.cmov.acmeterminal.data.DataRepository
import org.feup.cmov.acmeterminal.data.Status

class MainViewModel @ViewModelInject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    fun handleData(data: String?) {
        viewModelScope.launch {
            data ?: return@launch
            println("GOT $data")

            val result = dataRepository.submitOrder(data)
            when (result.status) {
                Status.LOADING -> { }
                Status.SUCCESS -> println("RES ${result.data}")
                Status.ERROR -> println("ERROR ${result.message}")
            }
        }
    }
}