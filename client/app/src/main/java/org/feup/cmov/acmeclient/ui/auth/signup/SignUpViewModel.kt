package org.feup.cmov.acmeclient.ui.auth.signup

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.data.DataRepository

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
        val nameError = when {
            name.isEmpty() -> R.string.error_required
            else -> null
        }

        val isValid = nameError == null

        _formState.value = _formState.value?.copy(
            nameError = nameError,
            isValid = _formState.value!!.isValid && isValid
        )
    }

    fun checkBillingInfo(nif: String, ccNumber: String, ccExpiration: String, ccCVV: String) {
        val nifError = when {
            nif.isEmpty() -> R.string.error_required
            nif.length != 9 -> R.string.error_invalid
            else -> null
        }

        val ccNumberError = when {
            ccNumber.isEmpty() -> R.string.error_required
            ccNumber.length != 16 -> R.string.error_invalid
            else -> null
        }

        val ccExpirationError = when {
            ccExpiration.isEmpty() -> R.string.error_required
            ccExpiration.length < 4 || ccExpiration.length > 5 -> R.string.error_invalid
            else -> null
        }

        val ccCVVError = when {
            ccCVV.isEmpty() -> R.string.error_required
            ccCVV.length != 3 -> R.string.error_invalid
            else -> null
        }

        val isValid = nifError == null && ccNumberError == null && ccExpirationError == null&& ccCVVError == null

        _formState.value = _formState.value?.copy(
            nifError = nifError,
            ccNumberError = ccNumberError,
            ccExpirationError = ccExpirationError,
            ccCVVError = ccCVVError,
            isValid = _formState.value!!.isValid && isValid
        )
    }

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

    fun signUp(
        name: String,
        nif: String, ccNumber: String, ccExpiration: String, ccCVV: String,
        username: String, password: String
    ) {
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