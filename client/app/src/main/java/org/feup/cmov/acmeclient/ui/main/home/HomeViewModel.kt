package org.feup.cmov.acmeclient.ui.main.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.feup.cmov.acmeclient.adapter.Content
import org.feup.cmov.acmeclient.data.DataRepository
import org.feup.cmov.acmeclient.data.Resource
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.data.cache.CachedOrder
import org.feup.cmov.acmeclient.data.model.Item

class HomeViewModel @ViewModelInject constructor(
//    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

    data class CartState(
        val show: Boolean = false,
        val price: Double = 0.0,
        val itemsCount: Int = 0
    )

    val items: LiveData<Resource<List<Content<Item>>>> = dataRepository.getItems()
        .combine(dataRepository.getOrder()) { items, order ->
            when (items.status) {
                Status.LOADING -> {
                    Resource.loading(null)
                }
                Status.SUCCESS -> {
                    Resource.success(items.data?.map {
                        Content(it.id, it, it.id in order.items.keys)
                    })
                }
                Status.ERROR -> {
                    Resource.error(items.message!!)
                }
            }
        }
        .asLiveData()

    val order: LiveData<CachedOrder> = dataRepository.getOrder()
        .distinctUntilChanged()
        .asLiveData()

    val cardState: LiveData<CartState> = dataRepository.getItems()
        .combine(dataRepository.getOrder()) { items, order ->
            when (items.status) {
                Status.SUCCESS -> {
                    var cartPrice = 0.0

                    items.data?.forEach {
                        cartPrice += it.price!! * order.items.getOrDefault(it.id, 0)
                    }

                    CartState(order.items.isNotEmpty(), cartPrice, order.items.count())
                }
                else -> {
                    CartState()
                }
            }
        }
        .asLiveData()

    private val _refreshing = MutableLiveData<Boolean>()
    val refreshing: LiveData<Boolean> =_refreshing

    fun fetchItems() {
        viewModelScope.launch {
            _refreshing.value = true
            dataRepository.fetchItems()
            _refreshing.value = false
        }
    }

    fun saveItemToOrder(item: Item, quantity: Int) {
        viewModelScope.launch {
            dataRepository.saveItemToOrder(item, quantity)
        }
    }
}

