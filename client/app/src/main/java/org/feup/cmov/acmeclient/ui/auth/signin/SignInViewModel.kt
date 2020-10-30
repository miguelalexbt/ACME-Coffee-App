package org.feup.cmov.acmeclient.ui.auth.signin

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.data.*
import org.feup.cmov.acmeclient.data.event.UiEvent

class SignInViewModel @ViewModelInject constructor(
//    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

    data class FormState(
        val usernameError: Int? = R.string.empty_string,
        val passwordError: Int? = R.string.empty_string,
        val isValid: Boolean = false
    )

    val authState: LiveData<Boolean?> = dataRepository.isLoggedIn.asLiveData()

    private val _uiEvent = MutableLiveData<UiEvent>()
    val uiEvent: LiveData<UiEvent> = _uiEvent

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

        _uiEvent.value = UiEvent(isLoading = true)

        viewModelScope.launch {
            val result = dataRepository.signIn(username, password)
            when (result.status) {
                Status.SUCCESS -> {
                    _uiEvent.value = UiEvent()
                }
                Status.ERROR -> {
                    when (result.message) {
                        "wrong_credentials" ->
                            _uiEvent.value = UiEvent(error = R.string.error_wrong_credentials)
                        else ->
                            _uiEvent.value = UiEvent(error = R.string.error_unknown)
                    }
                }
            }
        }
    }
}