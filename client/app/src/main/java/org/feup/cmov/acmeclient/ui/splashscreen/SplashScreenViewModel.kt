package org.feup.cmov.acmeclient.ui.splashscreen

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import org.feup.cmov.acmeclient.data.DataRepository

class SplashScreenViewModel @ViewModelInject constructor(
//    @Assisted savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {
    val authState: LiveData<Boolean> = dataRepository.isLoggedIn.asLiveData()
}
