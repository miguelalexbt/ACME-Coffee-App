package org.feup.cmov.acmeclient.ui.auth.signup

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.data.DataRepository
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.data.event.UiEvent

class SignUpViewModel @ViewModelInject constructor(
//    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

    data class FormState(
        val nameError: Int? = R.string.empty_string,
        val nifError: Int? = R.string.empty_string,
        val ccNumberError: Int? = R.string.empty_string,
        val ccCVVError: Int? = R.string.empty_string,
        val ccExpirationMonthError: Int? = R.string.empty_string,
        val ccExpirationYearError: Int? = R.string.empty_string,
        val usernameError: Int? = R.string.empty_string,
        val passwordError: Int? = R.string.empty_string,
    )

    val authState: LiveData<Boolean?> = dataRepository.isLoggedIn.asLiveData()

    private val _uiEvent = MutableLiveData<UiEvent>()
    val uiEvent: LiveData<UiEvent> = _uiEvent

    private val _formState = MutableLiveData(FormState())
    val formState: LiveData<FormState> = _formState

    fun signUp(
        name: String, nif: String, ccNumber: String, ccCVV: String,
        ccExpirationMonth: String, ccExpirationYear: String, username: String, password: String
    ) {
        if (!checkForm(name, nif, ccNumber, ccCVV, ccExpirationMonth, ccExpirationYear, username, password))
            return

        _uiEvent.value = UiEvent(isLoading = true)

        viewModelScope.launch {
            val result = dataRepository.signUp(
                name, nif, ccNumber, ccCVV,
                "${ccExpirationMonth.padStart(2, '0')}/${ccExpirationYear.takeLast(2)}",
                username, password
            )

            when (result.status) {
                Status.LOADING -> { }
                Status.SUCCESS -> {
                    _uiEvent.value = UiEvent()
                }
                Status.ERROR -> {
                    when (result.message) {
                        "username_taken" ->
                            _uiEvent.value = UiEvent(error = R.string.error_username_taken)
                        "weak_password" ->
                            _uiEvent.value = UiEvent(error = R.string.error_weak_password)
                        else ->
                            _uiEvent.value = UiEvent(error = R.string.error_unknown)
                    }
                }
            }
        }
    }

    private fun checkForm(
        name: String, nif: String, ccNumber: String, ccCVV: String,
        ccExpirationMonth: String, ccExpirationYear: String, username: String, password: String
    ): Boolean {
        val nameError = when {
            name.isEmpty() -> R.string.error_required
            else -> null
        }

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

        val ccCVVError = when {
            ccCVV.isEmpty() -> R.string.error_required
            ccCVV.length != 3 -> R.string.error_invalid
            else -> null
        }

        val ccExpirationMonthError = when {
            ccExpirationMonth.isEmpty() -> R.string.error_required
            else -> null
        }

        val ccExpirationYearError = when {
            ccExpirationYear.isEmpty() -> R.string.error_required
            else -> null
        }

        val usernameError = when {
            username.isEmpty() -> R.string.error_required
            else -> null
        }

        val passwordError = when {
            password.isEmpty() -> R.string.error_required
            else -> null
        }

        _formState.value = FormState(
            nameError, nifError, ccNumberError, ccCVVError,
            ccExpirationMonthError, ccExpirationYearError,
            usernameError, passwordError
        )

        return nameError == null && nifError == null && ccNumberError == null && ccCVVError == null &&
                ccExpirationMonthError == null && ccExpirationYearError == null &&
                usernameError == null && passwordError == null
    }
}