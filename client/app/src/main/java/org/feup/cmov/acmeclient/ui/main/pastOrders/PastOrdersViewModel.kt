package org.feup.cmov.acmeclient.ui.main.pastOrders

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.adapter.Content
import org.feup.cmov.acmeclient.data.DataRepository
import org.feup.cmov.acmeclient.data.Resource
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.data.event.UiEvent
import org.feup.cmov.acmeclient.data.model.Receipt
import org.feup.cmov.acmeclient.ui.main.checkout.ItemView
import org.feup.cmov.acmeclient.ui.main.checkout.VoucherView
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class PastOrdersViewModel @ViewModelInject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _uiEvent = MutableLiveData<UiEvent>()
    val uiEvent: LiveData<UiEvent> = _uiEvent

    val pastOrders: LiveData<Resource<List<Content<PastOrderView>>>> =
        dataRepository.getPastOrders()
            .map { orders ->
                when (orders.status) {
                    Status.LOADING -> {
                        Resource.loading(null)
                    }
                    Status.SUCCESS -> {
                        Resource.success(orders.data?.map {
                            Content(
                                it.id,
                                PastOrderView(
                                    it.id,
                                    it.number,
                                    it.items.size,
                                    DateTimeFormatter
                                        .ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH)
                                        .format(ZonedDateTime.parse(it.createdAt)),
                                    it.total,
                                    it.items,
                                    it.vouchers
                                )
                            )
                        })
                    }
                    Status.ERROR -> {
                        _uiEvent.value = UiEvent(error = R.string.error_unknown)
                        Resource.error(orders.message!!)
                    }
                }
            }
            .asLiveData()

    private val _refreshing = MutableLiveData<Boolean>()
    val refreshing: LiveData<Boolean> = _refreshing

    fun fetchPastOrders() {
        viewModelScope.launch {
            _refreshing.value = true
            dataRepository.fetchPastOrders()
            _refreshing.value = false
        }
    }

    fun getOrderReceipt(orderId: String): LiveData<Resource<Receipt>> =
        dataRepository.getReceipt(orderId).asLiveData()

    fun getOrderItems(orderItemMap: Map<String, Int>): LiveData<Resource<List<Content<ItemView>>>> {
        return flowOf(orderItemMap)
            .combine(dataRepository.getItemsAsMap()) { orderItems, allItems ->
                when (allItems.status) {
                    Status.LOADING -> {
                        Resource.loading(null)
                    }
                    Status.SUCCESS -> {
                        Resource.success(orderItems.keys.map {
                            val item = allItems.data!![it] ?: error("no item key ${it}")
                            val itemView = ItemView(
                                item.name,
                                item.type.capitalize(Locale.ENGLISH),
                                item.price,
                                orderItems[it] ?: error("no quantity for item ${it}")
                            )
                            Content(it, itemView)
                        })
                    }
                    Status.ERROR -> {
                        _uiEvent.value = UiEvent(error = R.string.error_unknown)
                        Resource.error(allItems.message!!)
                    }
                }
            }
            .asLiveData()
    }

//    fun getOrderVouchers(orderVouchers: Set<String>): LiveData<Resource<List<Content<VoucherView>>>> {
//        return flowOf(orderVouchers)
//            .combine(dataRepository.getVouchersAsMap()) { orderItems, allVouchers ->
//                when (allVouchers.status) {
//                    Status.LOADING -> {
//                        Resource.loading(null)
//                    }
//                    Status.SUCCESS -> {
//                        Resource.success(orderItems.map {
//                            val voucher = allVouchers.data!![it] ?: error("no voucher key ${it}")
//                            Content(it, VoucherView(voucher.type))
//                        })
//                    }
//                    Status.ERROR -> {
//                        _uiEvent.value = UiEvent(error = R.string.error_unknown)
//                        Resource.error(allVouchers.message!!)
//                    }
//                }
//            }
//            .asLiveData()
//    }

}