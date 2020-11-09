package org.feup.cmov.acmeclient.ui.main.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.feup.cmov.acmeclient.adapter.Content
import org.feup.cmov.acmeclient.data.DataRepository
import org.feup.cmov.acmeclient.data.Resource
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.data.cache.CachedOrder
import java.util.*

class HomeViewModel @ViewModelInject constructor(
//    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

    data class CartState(
        val show: Boolean = false,
        val price: Double = 0.0,
        val itemsCount: Int = 0
    )

    private val searchQuery = MutableLiveData("")

    fun setQuery(query: String) {
        searchQuery.value = query
    }

    private val categoriesFilter = MutableLiveData<List<String>>(emptyList())

    fun setCategoriesFilter(filter: List<String>) {
        categoriesFilter.value = filter
    }

    val itemsQuery: LiveData<Resource<List<Content<ItemView>>>> = dataRepository.getItems()
        .combine(searchQuery.asFlow().distinctUntilChanged()) { items, query ->
            when (items.status) {
                Status.SUCCESS -> {
                    val filteredItems =
                        items.data?.filter { item ->
                            item.name.toLowerCase().contains(query.toLowerCase())
                        }.takeIf { query != "" }
                    Resource.success(filteredItems ?: items.data)
                }
                else -> items
            }
        }
        .combine(categoriesFilter.asFlow().distinctUntilChanged()) { items, categories ->
            println(categories)
            when (items.status) {
                Status.SUCCESS -> {
                    val filteredItems =
                        items.data?.filter { item ->
                            item.type in categories
                        }.takeIf { categories.isNotEmpty() }
                    Resource.success(filteredItems ?: items.data)
                }
                else -> items
            }
        }
        .combine(dataRepository.getOrder()) { items, order ->
            when (items.status) {
                Status.LOADING -> {
                    Resource.loading(null)
                }
                Status.SUCCESS -> {
                    Resource.success(items.data?.map {
                        val itemView = ItemView(it.id, it.name, it.price, it.id in order.items.keys)
                        Content(it.id, itemView)
                    })
                }
                Status.ERROR -> {
                    Resource.error(items.message!!)
                }
            }
        }
        .asLiveData()

//    val items: LiveData<Resource<List<Content<Item>>>> = dataRepository.getItems()
//        .combine(dataRepository.getOrder()) { items, order ->
//            when (items.status) {
//                Status.LOADING -> {
//                    Resource.loading(null)
//                }
//                Status.SUCCESS -> {
//                    Resource.success(items.data?.map {
//                        Content(it.id, it, it.id in order.items.keys)
//                    })
//                }
//                Status.ERROR -> {
//                    Resource.error(items.message!!)
//                }
//            }
//        }
//        .asLiveData()

    val categories: LiveData<Resource<Map<String, Boolean>>> = dataRepository.getItems()
        .combine(categoriesFilter.asFlow().distinctUntilChanged()) { items, filter ->
            when (items.status) {
                Status.LOADING -> {
                    Resource.loading(null)
                }
                Status.SUCCESS -> {
                    Resource.success(items.data!!.map {
                        it.type
                    }.distinct().map {
                        it to (it in filter)
                    }.toMap())
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

    val cartState: LiveData<CartState> = dataRepository.getItems()
        .combine(dataRepository.getOrder()) { items, order ->
            when (items.status) {
                Status.SUCCESS -> {
                    var cartPrice = 0.0

                    items.data?.forEach {
                        cartPrice += it.price * order.items.getOrDefault(it.id, 0)
                    }

                    CartState(order.items.isNotEmpty(), cartPrice, order.items.values.sum())
                }
                else -> {
                    CartState()
                }
            }
        }
        .asLiveData()

    private val _refreshing = MutableLiveData<Boolean>()
    val refreshing: LiveData<Boolean> = _refreshing

    fun fetchItems() {
        viewModelScope.launch {
            _refreshing.value = true
            dataRepository.fetchItems()
            _refreshing.value = false
        }
    }

    fun saveItemToOrder(item: ItemView, quantity: Int) {
        viewModelScope.launch {
            dataRepository.saveItemToOrder(item.id, quantity)
        }
    }
}

