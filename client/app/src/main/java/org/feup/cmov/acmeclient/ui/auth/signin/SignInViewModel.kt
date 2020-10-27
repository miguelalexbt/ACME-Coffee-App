package org.feup.cmov.acmeclient.ui.auth.signin

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.data.*
import org.feup.cmov.acmeclient.data.api.ApiResponse
import org.feup.cmov.acmeclient.data.api.SignInRequest

class SignInViewModel @ViewModelInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

    data class ApiState(
        val success: Boolean = false,
        val error: Int? = null
    )

    data class FormState(
        val usernameError: Int? = R.string.empty_string,
        val passwordError: Int? = R.string.empty_string,
        val isValid: Boolean = false
    )

    private val _apiState = MutableLiveData(ApiState())
    val apiState: LiveData<ApiState> = _apiState

    private val _formState = MutableLiveData(FormState())
    val formState: LiveData<FormState> = _formState

    fun checkUsername(username: String) {
        val usernameError = when {
            username.isEmpty() -> R.string.error_required
            else -> null
        }

        _formState.value = _formState.value?.copy(usernameError = usernameError)

        checkValid()
    }

    fun checkPassword(password: String) {
        val passwordError = when {
            password.isEmpty() -> R.string.error_required
            else -> null
        }

        _formState.value = _formState.value?.copy(passwordError = passwordError)

        checkValid()
    }

    private fun checkValid() {
        val state = _formState.value!!

        val isValid = state.usernameError == null && state.passwordError == null

        _formState.value = _formState.value?.copy(isValid = isValid)
    }

    fun signIn(username: String, password: String) {
        if (!_formState.value!!.isValid) return

        viewModelScope.launch {
            val apiCall = dataRepository.signIn(
                username,
                password
            )

            when (apiCall) {
                is ApiResponse.Success ->
                    _apiState.value = ApiState(success = true)
                is ApiResponse.ApiError ->
                    _apiState.value = ApiState(error = R.string.error_wrong_credentials)
                is ApiResponse.NetworkError ->
                    _apiState.value = ApiState(error = R.string.error_unknown)
            }
        }
    }
}