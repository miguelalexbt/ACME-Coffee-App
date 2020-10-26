package org.feup.cmov.acmeclient.ui.auth.signin

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.feup.cmov.acmeclient.data.*
import org.feup.cmov.acmeclient.data.api.ApiResponse
import org.feup.cmov.acmeclient.data.model.Item

class SignInViewModel @ViewModelInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

    data class State(
        val success: Boolean = false,
        val error: String? = null
    )

    // Form state
    val username = MutableLiveData("")
    val password = MutableLiveData("")

    // Sign in state
    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    fun signIn() {
        viewModelScope.launch {
            when (val apiCall = dataRepository.signIn(username.value!!, password.value!!)) {
                is ApiResponse.Success -> _state.postValue(State(success = true))
                is ApiResponse.ApiError -> _state.postValue(State(error = apiCall.error))
                is ApiResponse.NetworkError -> _state.postValue(State(error = apiCall.error))
            }
        }
    }
}