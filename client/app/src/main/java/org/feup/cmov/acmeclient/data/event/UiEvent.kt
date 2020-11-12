package org.feup.cmov.acmeclient.data.event

data class UiState(val isLoading: Boolean, val error: Int?)

class UiEvent(val isLoading: Boolean = false, val error: Int? = null) :
    Event<UiState>(UiState(isLoading, error))