package org.feup.cmov.acmeclient.ui.auth.signup

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.data.DataRepository
import org.feup.cmov.acmeclient.data.api.ApiResponse
import org.feup.cmov.acmeclient.ui.auth.signin.SignInViewModel

class SignUpViewModel @ViewModelInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

    data class ApiState(
        val success: Boolean = false,
        val error: Int? = null
    )

    data class FormState(
        val nameError: Int? = null,
        val nifError: Int? = null,
        val ccNumberError: Int? = null,
        val ccExpirationError: Int? = null,
        val ccCVVError: Int? = null,
        val usernameError: Int? = null,
        val passwordError: Int? = null,
        val isValid: Boolean = false
    )

    private val _apiState = MutableLiveData(ApiState())
    val apiState: LiveData<ApiState> = _apiState

    private val _formState = MutableLiveData(FormState())
    val formState: LiveData<FormState> = _formState

    fun checkPersonalInfo(name: String) {
        _formState.value = _formState.value?.copy(
            nameError = if (name.isEmpty()) R.string.error_empty_name else null,
            isValid = _formState.value!!.isValid && name.isNotEmpty()
        )
    }

    fun checkBillingInfo() {

    }

    fun checkCredentials(username: String, password: String) {
        _formState.value = _formState.value?.copy(
            usernameError = if (username.isEmpty()) R.string.error_empty_username else null,
            passwordError = if (password.isEmpty()) R.string.error_empty_password else null,
            isValid = _formState.value!!.isValid && username.isNotEmpty() && password.isNotEmpty()
        )
    }

    fun signUp() {
        viewModelScope.launch {
//            when (dataRepository.signUp(username, password)) {
//                is ApiResponse.Success ->
//                    _apiState.value = SignInViewModel.ApiState(success = true)
//                is ApiResponse.ApiError ->
//                    _apiState.value =
//                        SignInViewModel.ApiState(error = R.string.error_wrong_credentials)
//                is ApiResponse.NetworkError ->
//                    _apiState.value = SignInViewModel.ApiState(error = R.string.error_unknown)
//            }
        }
    }


}