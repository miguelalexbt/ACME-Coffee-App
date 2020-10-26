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

    fun checkForm(username: String, password: String) {
        when {
            username.isEmpty() -> _formState.postValue(FormState(usernameError = R.string.error_empty_username))
            password.isEmpty() -> _formState.postValue(FormState(passwordError = R.string.error_empty_password))
            else -> _formState.postValue(FormState(isValid = true))
        }
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