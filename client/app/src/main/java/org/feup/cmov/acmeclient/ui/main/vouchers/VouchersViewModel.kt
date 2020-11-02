package org.feup.cmov.acmeclient.ui.main.vouchers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.feup.cmov.acmeclient.data.DataRepository
import org.feup.cmov.acmeclient.data.model.Voucher

class VouchersViewModel @ViewModelInject constructor(
//    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _refreshing = MutableLiveData<Boolean>()
    val refreshing: LiveData<Boolean> =_refreshing

    fun fetchVouchers() {
        viewModelScope.launch {
            _refreshing.value = true
            dataRepository.fetchVouchers()
            _refreshing.value = false
        }
    }

    fun toggleVoucher(voucher: Voucher) {
//        viewModelScope.launch {
//            dataRepository.updateOrder(item.id, 1)
//        }
    }
}