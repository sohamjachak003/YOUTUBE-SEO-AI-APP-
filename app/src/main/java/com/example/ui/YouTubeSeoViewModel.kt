package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiSeoService
import com.example.model.AiSeoResult
import com.example.model.ChannelProfile
import com.example.model.YouTubeVideo
import com.example.repository.YouTubeChannelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface UiState {
    object Disconnected : UiState
    object Connecting : UiState
    data class Connected(
        val profile: ChannelProfile,
        val videos: List<YouTubeVideo>,
        val selectedVideo: YouTubeVideo? = null,
        val isOptimizing: Boolean = false,
        val seoResult: AiSeoResult? = null,
        val activeTab: DashboardTab = DashboardTab.VIDEOS,
        val statusMessage: String? = null
    ) : UiState
}

enum class DashboardTab(val label: String) {
    VIDEOS("Recent Videos"),
    SEO_STUDIO("AI SEO Studio"),
    AI_TEAM("AI Channel Manager"),
    STRATEGY("Strategy & Reports")
}

class YouTubeSeoViewModel : ViewModel() {

    private val repository = YouTubeChannelRepository()
    private val geminiService = GeminiSeoService()

    private val _uiState = MutableStateFlow<UiState>(UiState.Disconnected)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun connectYouTubeOAuth() {
        viewModelScope.launch {
            _uiState.value = UiState.Connecting
            repository.connectChannel()
            val profile = repository.channelProfile.value
            val videos = repository.videos.value
            if (profile != null) {
                _uiState.value = UiState.Connected(
                    profile = profile,
                    videos = videos,
                    selectedVideo = videos.firstOrNull(),
                    statusMessage = "Connected successfully via Google OAuth 2.0 (Scopes: youtube.readonly, youtube.force-ssl)"
                )
                // Proactively run AI optimization on the first video
                videos.firstOrNull()?.let { optimizeVideo(it) }
            } else {
                _uiState.value = UiState.Disconnected
            }
        }
    }

    fun disconnect() {
        repository.disconnectChannel()
        _uiState.value = UiState.Disconnected
    }

    fun selectVideo(video: YouTubeVideo) {
        val currentState = _uiState.value
        if (currentState is UiState.Connected) {
            _uiState.value = currentState.copy(
                selectedVideo = video,
                activeTab = DashboardTab.SEO_STUDIO
            )
            optimizeVideo(video)
        }
    }

    fun setActiveTab(tab: DashboardTab) {
        val currentState = _uiState.value
        if (currentState is UiState.Connected) {
            _uiState.value = currentState.copy(activeTab = tab)
        }
    }

    fun optimizeVideo(video: YouTubeVideo) {
        val currentState = _uiState.value
        if (currentState is UiState.Connected) {
            viewModelScope.launch {
                _uiState.value = currentState.copy(isOptimizing = true)
                val result = geminiService.optimizeVideo(video)
                val newCurrent = _uiState.value
                if (newCurrent is UiState.Connected) {
                    _uiState.value = newCurrent.copy(
                        isOptimizing = false,
                        seoResult = result.getOrNull()
                    )
                }
            }
        }
    }

    fun clearStatusMessage() {
        val currentState = _uiState.value
        if (currentState is UiState.Connected) {
            _uiState.value = currentState.copy(statusMessage = null)
        }
    }
}
