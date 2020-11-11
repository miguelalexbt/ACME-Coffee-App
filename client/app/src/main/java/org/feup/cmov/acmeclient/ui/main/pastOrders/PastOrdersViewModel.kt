package org.feup.cmov.acmeclient.ui.main.pastOrders

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.feup.cmov.acmeclient.adapter.Content
import org.feup.cmov.acmeclient.data.DataRepository
import org.feup.cmov.acmeclient.data.Resource
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.data.model.PastOrder
import org.feup.cmov.acmeclient.data.model.Receipt

class PastOrdersViewModel @ViewModelInject constructor(
//    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

    val pastOrders: LiveData<Resource<List<Content<PastOrderView>>>> = dataRepository.getPastOrders()
        .map { orders ->
            when (orders.status) {
                Status.LOADING -> {
                    Resource.loading(null)
                }
                Status.SUCCESS -> {
                    Resource.success(orders.data?.map {
                        Content(it.id, PastOrderView(it.id, it.items.length, it.createdAt, it.total))
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

    fun getOrderReceipt(order: PastOrder): Flow<Resource<Receipt>> = dataRepository.getReceipt(order.id)

}