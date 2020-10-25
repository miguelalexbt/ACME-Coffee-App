package org.feup.cmov.acmeclient.ui.main.vouchers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VouchersViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is vouchers Fragment"
    }
    val text: LiveData<String> = _text
}