package org.feup.cmov.acmeclient.ui.main.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.feup.cmov.acmeclient.adapter.Content
import org.feup.cmov.acmeclient.data.DataRepository
import org.feup.cmov.acmeclient.data.Resource
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.data.cache.CachedOrder


class HomeViewModel @ViewModelInject constructor(
//    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

    data class CartState(
        val show: Boolean = false,
        val price: Double = 0.0,
        val itemsCount: Int = 0
    )

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

    val areFiltersActive: LiveData<Boolean> =
        _categoriesFilter.asFlow().combine(_showOnlyFavorites.asFlow()) { categoriesFilter, show ->
            categoriesFilter.isNotEmpty() || show
        }.asLiveData()

    val itemsQuery: LiveData<Resource<List<Content<ItemView>>>> = dataRepository.getItems()
        .combine(_searchQuery.asFlow().distinctUntilChanged()) { items, query ->
            println("items -> $items")
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
                            dataRepository.loggedInUser?.userId in Gson().fromJson(
                                item.usersFavorite,
                                object : TypeToken<Set<String>>() {}.type
                            ) as Set<String>
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
                            Gson().fromJson(
                                it.usersFavorite,
                                object : TypeToken<Set<String?>>() {}.type
                            )
                        )
                        Content(it.id, itemView)
                    })
                }
                Status.ERROR -> {
                    Resource.error(items.message!!)
                }
            }
        }
        .asLiveData()

    val categories: LiveData<Resource<Map<String, Boolean>>> = dataRepository.getItems()
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

    fun toggleFavoriteItem(item: ItemView) {
        viewModelScope.launch {
            dataRepository.toggleFavoriteItem(item)
        }
    }

}

