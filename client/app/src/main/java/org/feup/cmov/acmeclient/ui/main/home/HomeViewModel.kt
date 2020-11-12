package org.feup.cmov.acmeclient.ui.main.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.adapter.Content
import org.feup.cmov.acmeclient.data.DataRepository
import org.feup.cmov.acmeclient.data.Resource
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.data.cache.CachedOrder
import org.feup.cmov.acmeclient.data.event.UiEvent

class HomeViewModel @ViewModelInject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    data class CartState(
        val show: Boolean = false,
        val price: Double = 0.0,
        val itemsCount: Int = 0
    )

    private val _uiEvent = MutableLiveData<UiEvent>()
    val uiEvent: LiveData<UiEvent> = _uiEvent

    private val _searchQuery = MutableLiveData("")

    fun setQuery(query: String) {
        _searchQuery.value = query
    }

    private val _categoriesFilter = MutableLiveData<List<String>>(emptyList())

    fun setCategoriesFilter(filter: List<String>) {
        _categoriesFilter.value = filter
    }

    private val _showOnlyFavorites = MutableLiveData<Boolean>(false)

    fun setShowOnlyFavorites(show: Boolean) {
        _showOnlyFavorites.value = show
    }

    val areFiltersActive = _categoriesFilter.asFlow().zip(_showOnlyFavorites.asFlow()) { l1, l2 -> l1.isNotEmpty() || l2 }.asLiveData()

    val itemsQuery: LiveData<Resource<List<Content<ItemView>>>> = dataRepository.getItems()
        .combine(_searchQuery.asFlow().distinctUntilChanged()) { items, query ->
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
        .combine(_categoriesFilter.asFlow().distinctUntilChanged()) { items, categories ->
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
        .combine(_showOnlyFavorites.asFlow().distinctUntilChanged()) { items, showOnlyFavorites ->
            when (items.status) {
                Status.SUCCESS -> {
                    val filteredItems =
                        items.data?.filter { item ->
                            dataRepository.loggedInUser?.userId in item.usersFavorite
                        }.takeIf { showOnlyFavorites }
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
                        val itemView = ItemView(
                            it.id,
                            it.name,
                            it.price,
                            it.id in order.items.keys,
                            dataRepository.loggedInUser!!.userId in it.usersFavorite,
                            it.usersFavorite
                        )
                        Content(it.id, itemView)
                    })
                }
                Status.ERROR -> {
                    _uiEvent.value = UiEvent(error = R.string.error_unknown)
                    Resource.error(items.message!!)
                }
            }
        }
        .asLiveData()

    val categories: LiveData<Resource<Map<String, Boolean>>> = dataRepository.getItems(fetch = false)
        .combine(_categoriesFilter.asFlow().distinctUntilChanged()) { items, filter ->
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
                    _uiEvent.value = UiEvent(error = R.string.error_unknown)
                    Resource.error(items.message!!)
                }
            }
        }
        .asLiveData()

    val order: LiveData<CachedOrder> = dataRepository.getOrder()
        .distinctUntilChanged()
        .asLiveData()

    val cartState: LiveData<CartState> = dataRepository.getItems(fetch = false)
        .combine(dataRepository.getOrder()) { items, order ->
            when (items.status) {
                Status.LOADING -> {
                    CartState()
                }
                Status.SUCCESS -> {
                    var cartPrice = 0.0

                    items.data?.forEach {
                        cartPrice += it.price * order.items.getOrDefault(it.id, 0)
                    }

                    CartState(order.items.isNotEmpty(), cartPrice, order.items.values.sum())
                }
                Status.ERROR -> {
                    _uiEvent.value = UiEvent(error = R.string.error_unknown)
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

    fun toggleFavoriteItem(item: ItemView) {
        viewModelScope.launch {
            dataRepository.toggleFavoriteItem(item)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            dataRepository.signOut()
        }
    }
}

