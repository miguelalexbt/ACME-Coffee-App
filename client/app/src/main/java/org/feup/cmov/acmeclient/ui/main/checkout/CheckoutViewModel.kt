package org.feup.cmov.acmeclient.ui.main.checkout

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.*
import org.feup.cmov.acmeclient.adapter.Content
import org.feup.cmov.acmeclient.data.DataRepository
import org.feup.cmov.acmeclient.data.Resource
import org.feup.cmov.acmeclient.data.Status

class CheckoutViewModel @ViewModelInject constructor(
//    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

//    val order: LiveData<Resource<ByteString>> = dataRepository.generateOrderString().asLiveData()

    val items: LiveData<Resource<List<Content<ItemView>>>> = dataRepository.getOrder()
        .combine(dataRepository.getItems()) { order, items ->
            when (items.status) {
                Status.SUCCESS -> {
                    val itemsMap = items.data!!.associateBy({ it.id }, { it })
                    Resource.success(order.items.map {
                        val item = itemsMap[it.key] ?: error("no item key ${it.key}")
                        Content(item.id, ItemView(item.name!!, item.price!!, it.value), false)
                    })
                }
                else -> items as Resource<List<Content<ItemView>>>
            }
        }
        .asLiveData()

    val vouchers: LiveData<Resource<List<Content<VoucherView>>>> = dataRepository.getOrder()
        .combine(dataRepository.getVouchers(false)) { order, vouchers ->
            when (vouchers.status) {
                Status.SUCCESS -> {
                    val vouchersMap = vouchers.data!!.associateBy({ it.id }, { it })
                    Resource.success(
                        (order.offerVouchers + order.discountVoucher).map {
                            val voucher = vouchersMap[it] ?: error("no voucher key $it")
                            Content(voucher.id, VoucherView(voucher.type), false)
                        }
                    )
                }
                else -> vouchers as Resource<List<Content<VoucherView>>>
            }
        }
        .asLiveData()
}