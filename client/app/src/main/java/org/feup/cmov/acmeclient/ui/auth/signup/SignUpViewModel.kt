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
        val nameError: Int? = R.string.empty_string,
        val nifError: Int? = R.string.empty_string,
        val ccNumberError: Int? = R.string.empty_string,
        val ccExpirationError: Int? = R.string.empty_string,
        val ccCVVError: Int? = R.string.empty_string,
        val usernameError: Int? = R.string.empty_string,
        val passwordError: Int? = R.string.empty_string,
        val isValid: Boolean = false
    )

    private val _apiState = MutableLiveData(ApiState())
    val apiState: LiveData<ApiState> = _apiState

    private val _formState = MutableLiveData(FormState())
    val formState: LiveData<FormState> = _formState

    fun checkName(name: String) {
        val nameError = when {
            name.isEmpty() -> R.string.error_required
            else -> null
        }

        _formState.value = _formState.value?.copy(nameError = nameError)

        checkValid()
    }

    fun checkNif(nif: String) {
        val nifError = when {
            nif.isEmpty() -> R.string.error_required
            nif.length != 9 -> R.string.error_invalid
            else -> null
        }

        _formState.value = _formState.value?.copy(nifError = nifError)

        checkValid()
    }

    fun checkCcNumber(ccNumber: String) {
        val ccNumberError = when {
            ccNumber.isEmpty() -> R.string.error_required
            ccNumber.length != 16 -> R.string.error_invalid
            else -> null
        }

        _formState.value = _formState.value?.copy(ccNumberError = ccNumberError)

        checkValid()
    }

    fun checkCcExpiration(ccExpiration: String) {
        val ccExpirationError = when {
            ccExpiration.isEmpty() -> R.string.error_required
            ccExpiration.length < 4 || ccExpiration.length > 5 -> R.string.error_invalid
            else -> null
        }

        _formState.value = _formState.value?.copy(ccExpirationError = ccExpirationError)

        checkValid()
    }

    fun checkCcCVV(ccCVV: String) {
        val ccCVVError = when {
            ccCVV.isEmpty() -> R.string.error_required
            ccCVV.length != 3 -> R.string.error_invalid
            else -> null
        }

        _formState.value = _formState.value?.copy(ccCVVError = ccCVVError)

        checkValid()
    }

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

        val isValid = state.nameError == null && state.nifError == null &&
                state.ccNumberError == null && state.ccExpirationError == null &&
                state.ccCVVError == null &&
                state.usernameError == null && state.passwordError == null

        _formState.value = _formState.value?.copy(isValid = isValid)
    }

    fun signUp(
        name: String,
        nif: String, ccNumber: String, ccExpiration: String, ccCVV: String,
        username: String, password: String
    ) {
        if (!_formState.value!!.isValid) return

//        viewModelScope.launch {
//            val apiCall = dataRepository.signUp(
//                name,
//                nif, ccNumber, ccExpiration, ccCVV,
//                username, password
//            )
//
//            when (apiCall) {
//                is ApiResponse.Success ->
//                    _apiState.value = ApiState(success = true)
//                is ApiResponse.ApiError ->
//                    _apiState.value = ApiState(error = R.string.error_wrong_credentials)
//                is ApiResponse.NetworkError ->
//                    _apiState.value = ApiState(error = R.string.error_unknown)
//            }
//        }
    }


}