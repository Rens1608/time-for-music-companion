package com.timeformusic.companion.viewModels

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timeformusic.companion.repositories.SpotifyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpotifyViewModel @Inject constructor(
    private val spotifyRepository: SpotifyRepository,
) : ViewModel() {
    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isPlaying: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isConnected: MutableStateFlow<Boolean> = MutableStateFlow(spotifyRepository.checkIfConnected())
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _isSpotifyMissingModalOpen = MutableStateFlow<Boolean>(false)
    val isSpotifyMissingModalOpen: StateFlow<Boolean> = _isSpotifyMissingModalOpen

    fun connectToSpotify(onConnected: () -> Unit, onError: (Throwable) -> Unit) {
        spotifyRepository.connectToSpotifyAppRemote(
            {
                println("Viewmodel")
                _isConnected.value = true;
                onConnected()
            },
            onError
        )
    }

    fun authenticate(activity: Activity) {
        if (spotifyRepository.isSpotifyInstalled()){
            spotifyRepository.authenticate(activity) { token ->
                _isConnected.value = true;
            }
        } else {
            _isSpotifyMissingModalOpen.update { true }
        }
    }

    fun checkIfConnected() {
        _isConnected.value = spotifyRepository.checkIfConnected()
    }

    fun toggleTrack() {
        viewModelScope.launch {
            if (isPlaying.value) {
                spotifyRepository.pauseTrack()
                _isPlaying.value = false
            } else {
                spotifyRepository.resumeTrack()
                _isPlaying.value = true
            }
        }
    }

    fun playTrack(url: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            try {
                spotifyRepository.playTrack(
                    url,
                    onSuccess = {
                        _isPlaying.value = true
                        onSuccess()
                    },
                    onError = onError
                )
            } catch (e: Exception) {
                println(e)
                onError()
            }
        }
    }

    fun setIsLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    override fun onCleared() {
        super.onCleared()
        spotifyRepository.disconnect()
    }

    fun setIsSpotifyMissingModalOpen(isSpotifyMissingModalOpen: Boolean) {
        _isSpotifyMissingModalOpen.update {
            isSpotifyMissingModalOpen
        }
    }
}
