package org.feup.cmov.acmeclient.ui.main.checkout

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.zip
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.adapter.Content
import org.feup.cmov.acmeclient.data.DataRepository
import org.feup.cmov.acmeclient.data.Resource
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.data.event.UiEvent
import java.util.*

class CheckoutViewModel @ViewModelInject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _uiEvent = MutableLiveData<UiEvent>()
    val uiEvent: LiveData<UiEvent> = _uiEvent

    val items: LiveData<Resource<List<Content<ItemView>>>> = dataRepository.getOrder()
        .combine(dataRepository.getItemsAsMap()) { order, items ->
            when (items.status) {
                Status.LOADING -> {
                    Resource.loading(null)
                }
                Status.SUCCESS -> {
                    Resource.success(order.items.map {
                        val item = items.data!![it.key] ?: error("no item key ${it.key}")
                        val itemView = ItemView(
                            item.name,
                            item.type.capitalize(Locale.ENGLISH),
                            item.price,
                            it.value
                        )
                        Content(item.id, itemView)
                    })
                }
                Status.ERROR -> {
                    _uiEvent.value = UiEvent(error = R.string.error_unknown)
                    Resource.error(items.message!!)
                }
            }
        }
        .asLiveData()

    val vouchers: LiveData<Resource<List<Content<VoucherView>>>> = dataRepository.getOrder()
        .combine(dataRepository.getVouchersAsMap()) { order, vouchers ->
            when (vouchers.status) {
                Status.LOADING -> {
                    Resource.loading(null)
                }
                Status.SUCCESS -> {
                    val orderVouchers = if (order.discountVoucher != null)
                        order.offerVouchers + order.discountVoucher
                    else
                        order.offerVouchers

                    Resource.success(orderVouchers.map {
                        val voucher = vouchers.data!![it] ?: error("no voucher key $it")
                        Content(voucher.id, VoucherView(voucher.type))
                    })
                }
                Status.ERROR -> {
                    _uiEvent.value = UiEvent(error = R.string.error_unknown)
                    Resource.error(vouchers.message!!)
                }
            }
        }
        .asLiveData()

    var total: LiveData<Resource<Double>> = items.asFlow()
        .combine(vouchers.asFlow()) { itemViews, voucherViews ->
            if (itemViews.status != Status.SUCCESS || voucherViews.status != Status.SUCCESS)
                return@combine Resource.loading(null)

            var items = itemViews.data!!.map { it.content }
            val vouchers = voucherViews.data!!.map { it.content }

            var coffeeVouchers = vouchers.filter { it.type == 'o' }.count()
            val hasDiscountVoucher = vouchers.any { it.type == 'd' }

            // Ignore as many coffee items as coffee vouchers
            items = items.filter {
                if (it.type.toLowerCase() != "coffee") return@filter true

                if (coffeeVouchers > 0) {
                    coffeeVouchers -= 1; false
                } else {
                    true
                }
            }

            // Calculate total
            var total = items.fold(0.0, { acc, item ->
                acc + item.price * item.quantity
            })

            // Apply discount voucher
            if (hasDiscountVoucher) total *= 0.95

            Resource.success(total)
        }
        .asLiveData()

    private val isLoadingItems = items.map { it.status == Status.LOADING }.asFlow()
    private val isLoadingVouchers = vouchers.map { it.status == Status.LOADING }.asFlow()
    val isLoading = isLoadingItems.zip(isLoadingVouchers) { l1, l2 -> l1 && l2 }.asLiveData()
}