package org.feup.cmov.acmeclient.ui.signup

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import org.feup.cmov.acmeclient.data.UserRepository

data class State(
    val name: String = "",
    val username: String = "",
    val password: String = ""
)

class SignUpViewModel @ViewModelInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository
) : ViewModel() {

//    val userId: String? = savedStateHandle["uid"]

    private val _state = MutableLiveData<State>()
    val state : LiveData<State> = _state
    
    fun signUp() {
        userRepository.signUp(
            "Miguel Teixeira",
            "miguelalexbt",
            "123"
        )

        // Update state
        // ...

//        return result;
    }
 }