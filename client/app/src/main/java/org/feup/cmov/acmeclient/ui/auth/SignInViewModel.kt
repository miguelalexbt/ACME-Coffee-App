package org.feup.cmov.acmeclient.ui.auth

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.delay
import org.feup.cmov.acmeclient.data.DataRepository
import org.feup.cmov.acmeclient.data.model.Item

data class State(
    val txt: String = "miguelalexbt"
)

class SignInViewModel @ViewModelInject constructor(
//    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _state = MutableLiveData<String>("")
    val state: LiveData<String> = _state

    val items: LiveData<List<Item>> = dataRepository.getItems().asLiveData()

    fun signIn() {
        println("Value is ${_state.value}")
        _state.value = _state.value + "A"
    }
}