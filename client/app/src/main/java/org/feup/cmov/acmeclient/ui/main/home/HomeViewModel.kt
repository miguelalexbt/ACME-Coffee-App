package org.feup.cmov.acmeclient.ui.main.home

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.feup.cmov.acmeclient.data.DataRepository
import org.feup.cmov.acmeclient.data.Resource
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.data.model.Item

class HomeViewModel @ViewModelInject constructor(
//    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

    data class MarkedItem(val item: Item, val chosen: Boolean)

    private val _order = MutableLiveData<Map<String, Int>>(emptyMap())
    val order: Flow<Map<String, Int>> = _order.asFlow()

    val items: LiveData<Resource<List<MarkedItem>>> = dataRepository.getItems()
        .combine(order) { items, order ->
            when (items.status) {
                Status.LOADING -> {
                    Resource.loading<List<MarkedItem>>(null)
                }
                Status.SUCCESS -> {
                    Resource.success(items.data?.map { MarkedItem(it, it.id in order.keys) })
                }
                Status.ERROR -> {
                    Resource.error(items.message!!)
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

    fun toggleItem(item: Item) {
        _order.value = _order.value!!
            .toMutableMap()
            .apply {
                compute(item.id) { _, v -> if (v == null) 1 else null }
            }
    }
}