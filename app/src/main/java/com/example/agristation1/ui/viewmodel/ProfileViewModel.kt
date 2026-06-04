package com.example.agristation1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.agristation1.data.UserPreferencesRepository
import com.example.agristation1.data.farmDetails.FarmDetails
import com.example.agristation1.data.farmDetails.FarmDetailsOfflineRepository
import com.example.agristation1.data.farmDetails.FarmDetailsRepository
import com.example.agristation1.data.historyDetails.HistoryOfflineRepository
import com.example.agristation1.data.userDetails.UserDetails
import com.example.agristation1.data.userDetails.UserDetailsOfflineRepository
import com.example.agristation1.data.userDetails.UserDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val userDetails: UserDetails? = null,
    val isLightTheme: Boolean = true,
    val farmDetails: FarmDetails? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userDetailsOfflineRepository: UserDetailsRepository,
    private val farmDetailsOfflineRepository: FarmDetailsRepository
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        userDetailsOfflineRepository.getUserDetailsById(1),
        farmDetailsOfflineRepository.getFarmDetailsStream(),
        userPreferencesRepository.isLightTheme
    ) { userDetails, farmDetails, isLightTheme ->
        ProfileUiState(
            userDetails = userDetails,
            isLightTheme = isLightTheme,
            farmDetails = farmDetails
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState()
    )

    fun changeTheme(isLightTheme: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveIsLightTheme(isLightTheme)
        }
    }
}