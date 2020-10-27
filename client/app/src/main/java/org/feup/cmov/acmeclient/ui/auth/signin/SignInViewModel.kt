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

    data class ApiState(
        val success: Boolean = false,
        val error: Int? = null
    )

    data class FormState(
        val usernameError: Int? = null,
        val passwordError: Int? = null,
        val isValid: Boolean = false
    )

    private val _apiState = MutableLiveData(ApiState())
    val apiState: LiveData<ApiState> = _apiState

    private val _formState = MutableLiveData(FormState())
    val formState: LiveData<FormState> = _formState

    fun checkCredentials(username: String, password: String) {
        val usernameError = when {
            username.isEmpty() -> R.string.error_required
            else -> null
        }

        val passwordError = when {
            password.isEmpty() -> R.string.error_required
            else -> null
        }

        val isValid = usernameError == null && passwordError == null

        _formState.value = _formState.value?.copy(
            usernameError = usernameError,
            passwordError = passwordError,
            isValid = _formState.value!!.isValid && isValid
        )
    }
    fun signIn(username: String, password: String) {
        viewModelScope.launch {
            when (dataRepository.signIn(username, password)) {
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