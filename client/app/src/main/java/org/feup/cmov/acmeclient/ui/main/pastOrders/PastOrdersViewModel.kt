package org.feup.cmov.acmeclient.ui.main.pastOrders

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.feup.cmov.acmeclient.adapter.Content
import org.feup.cmov.acmeclient.data.DataRepository
import org.feup.cmov.acmeclient.data.Resource
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.data.model.PastOrder
import org.feup.cmov.acmeclient.data.model.Receipt
import org.feup.cmov.acmeclient.ui.main.checkout.ItemView
import org.feup.cmov.acmeclient.ui.main.checkout.VoucherView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class PastOrdersViewModel @ViewModelInject constructor(
//    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

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
                    else -> allItems as Resource<List<Content<ItemView>>>
                }
            }
            .asLiveData()
    }

    fun getOrderVouchers(orderVouchers: Set<String>) : LiveData<Resource<List<Content<VoucherView>>>> {
        return flowOf(orderVouchers)
            .combine(dataRepository.getVouchersAsMap()) { orderItems, allVouchers ->
                when (allVouchers.status) {
                    Status.SUCCESS -> {
                        Resource.success(orderItems.map {
                            val voucher = allVouchers.data!![it] ?: error("no voucher key ${it}")
                            Content(it, VoucherView(voucher.type))
                        })
                    }
                    else -> allVouchers as Resource<List<Content<VoucherView>>>
                }
            }
            .asLiveData()
    }

//
//    val vouchers: LiveData<Resource<List<Content<VoucherView>>>> = dataRepository.getOrder()
//        .combine(dataRepository.getVouchersAsMap()) { order, vouchers ->
//            when (vouchers.status) {
//                Status.SUCCESS -> {
//                    val orderVouchers = if (order.discountVoucher != null)
//                        order.offerVouchers + order.discountVoucher
//                    else
//                        order.offerVouchers
//
//                    Resource.success(orderVouchers.map {
//                        val voucher = vouchers.data!![it] ?: error("no voucher key $it")
//                        Content(voucher.id, VoucherView(voucher.type))
//                    })
//                }
//                else -> vouchers as Resource<List<Content<VoucherView>>>
//            }
//        }
//        .asLiveData()


}