package org.feup.cmov.acmeclient.ui.auth.signin

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.data.*
import org.feup.cmov.acmeclient.data.api.ApiResponse

class SignInViewModel @ViewModelInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

    data class State(
        val success: Boolean = false,
        val error: Int = R.string.empty_string,
    )

    // State
    private val _state = MutableLiveData(State())
    val state: LiveData<State> = _state

    fun signIn(username: String, password: String) {
        when {
            username.isEmpty() ->
                _state.postValue(State(error = R.string.error_empty_username))
            password.isEmpty() ->
                _state.postValue(State(error = R.string.error_empty_password))
            else -> {
                viewModelScope.launch {
                    when (dataRepository.signIn(username, password)) {
                        is ApiResponse.Success ->
                            _state.postValue(State(success = true))
                        is ApiResponse.ApiError ->
                            _state.postValue(State(error = R.string.error_wrong_credentials))
                        is ApiResponse.NetworkError ->
                            _state.postValue(State(error = R.string.error_wrong_credentials))
                    }
                }
            }
        }
    }
}