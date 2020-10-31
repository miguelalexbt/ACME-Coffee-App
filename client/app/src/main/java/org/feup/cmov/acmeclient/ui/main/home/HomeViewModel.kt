package org.feup.cmov.acmeclient.ui.main.home

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.feup.cmov.acmeclient.data.DataRepository
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.ui.auth.signin.SignInViewModel

class HomeViewModel @ViewModelInject constructor(
//    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

    // Sign in state
//    private val _state = MutableLiveData<SignInViewModel.State>()
//    val state: LiveData<SignInViewModel.State> = _state

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    val items: LiveData<List<Item>> = dataRepository.getItems().asLiveData()

//    init {
//        viewModelScope.launch {
//            println("FETCHING ITEMS")
//            dataRepository.fetchItems()
//        }
//    }

//    fun test() {
//        viewModelScope.launch {
//            dataRepository.fetchItems()
//        }
//    }
}