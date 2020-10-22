package org.feup.cmov.acmeclient.ui.signup

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.feup.cmov.acmeclient.data.DataRepository
import org.feup.cmov.acmeclient.data.model.User

data class State(
    val name: String = "",
    val username: String = "",
    val password: String = ""
)

class SignUpViewModel @ViewModelInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

//    val userId: String? = savedStateHandle["uid"]

    private val _state = MutableLiveData<State>()
    val state : LiveData<State> = _state
    
    fun signUp() {
        viewModelScope.launch {
            val result = dataRepository.signUp(
                    "Miguel Teixeira",
                    "miguelalexbt",
                    "123"
                )

            result
                .onSuccess {
                    // Update state (livedata)
                }
                .onFailure {
                    throw it.cause!!
                }
        }
    }
 }