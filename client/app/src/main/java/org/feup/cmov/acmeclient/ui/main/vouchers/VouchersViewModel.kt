package org.feup.cmov.acmeclient.ui.main.vouchers

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.feup.cmov.acmeclient.adapter.Content
import org.feup.cmov.acmeclient.data.DataRepository
import org.feup.cmov.acmeclient.data.Resource
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.data.model.Voucher

class VouchersViewModel @ViewModelInject constructor(
//    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

    val vouchers: LiveData<Resource<List<Content<Voucher>>>> = dataRepository.getVouchers()
        .combine(dataRepository.getOrder()) { vouchers, order ->
            when (vouchers.status) {
                Status.LOADING -> {
                    Resource.loading(null)
                }
                Status.SUCCESS -> {
                    Resource.success(vouchers.data?.map {
                        Content(it.id, it, it.id in order.offerVouchers || it.id == order.discountVoucher)
                    })
                }
                Status.ERROR -> {
                    Resource.error(vouchers.message!!)
                }
            }
        }
        .asLiveData()

    private val _refreshing = MutableLiveData<Boolean>()
    val refreshing: LiveData<Boolean> =_refreshing

    fun fetchVouchers() {
        viewModelScope.launch {
            _refreshing.value = true
            dataRepository.fetchVouchers()
            _refreshing.value = false
        }
    }

    fun toggleVoucher(voucher: Content<Voucher>) {
        viewModelScope.launch {
            dataRepository.addVoucherToOrder(voucher.content)
        }
    }
}