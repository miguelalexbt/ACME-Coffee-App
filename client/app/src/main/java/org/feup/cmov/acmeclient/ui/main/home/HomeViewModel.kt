package org.feup.cmov.acmeclient.ui.main.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.feup.cmov.acmeclient.adapter.Content
import org.feup.cmov.acmeclient.data.DataRepository
import org.feup.cmov.acmeclient.data.Resource
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.data.model.Item

class HomeViewModel @ViewModelInject constructor(
//    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

    val items: LiveData<Resource<List<Content<Item>>>> = dataRepository.getItems()
        .combine(dataRepository.getOrder()) { items, order ->
            when (items.status) {
                Status.LOADING -> {
                    Resource.loading<List<Content<Item>>>(null)
                }
                Status.SUCCESS -> {
                    Resource.success(items.data?.map {
                        Content(it.id, it, it.id in order.keys)
                    })
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
        viewModelScope.launch {
            dataRepository.updateOrder(item.id, 1)
        }
    }
}

