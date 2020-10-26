package org.feup.cmov.acmeclient.ui.main.vouchers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import org.feup.cmov.acmeclient.data.DataRepository

class VouchersViewModel @ViewModelInject constructor(
//    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is vouchers Fragment"
    }
    val text: LiveData<String> = _text

}