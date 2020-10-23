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

data class FormState(
    val usernameError: String,
    val passwordError: String
)

class SignUpViewModel @ViewModelInject constructor(
//    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {
    private val _formState = MutableLiveData<FormState>()
    val formState : LiveData<FormState> = _formState

    fun signUp(name: String, username: String, password: String) {
        viewModelScope.launch {
            val result = dataRepository.signUp(
                name, username, password
            )

//            result
//                .onSuccess {
//                    // Update state (livedata)
//                }
//                .onFailure {
//                    throw it.cause!!
//                }
        }
    }
 }