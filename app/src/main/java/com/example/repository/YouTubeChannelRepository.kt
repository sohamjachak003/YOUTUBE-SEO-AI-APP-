package com.example.repository

import com.example.model.ChannelProfile
import com.example.model.YouTubeVideo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class YouTubeChannelRepository {

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _channelProfile = MutableStateFlow<ChannelProfile?>(null)
    val channelProfile: StateFlow<ChannelProfile?> = _channelProfile.asStateFlow()

    private val _videos = MutableStateFlow<List<YouTubeVideo>>(emptyList())
    val videos: StateFlow<List<YouTubeVideo>> = _videos.asStateFlow()

    suspend fun connectChannel(channelName: String = "TechGrowth Insights", handle: String = "@techgrowth_ai") {
        delay(700) // Simulating secure OAuth 2.0 handshake and token exchange
        _channelProfile.value = ChannelProfile(
            id = "UC_AI_STUDIO_SAMPLE_CHANNEL",
            name = channelName,
            handle = handle,
            avatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=200&auto=format&fit=crop&q=80",
            subscriberCount = "142.8K",
            totalViews = "12.4M",
            videoCount = 86,
            niche = "Tech & AI Productivity"
        )
        _videos.value = getSampleChannelVideos()
        _isConnected.value = true
    }

    fun disconnectChannel() {
        _isConnected.value = false
        _channelProfile.value = null
        _videos.value = emptyList()
    }

    private fun getSampleChannelVideos(): List<YouTubeVideo> {
        return listOf(
            YouTubeVideo(
                id = "vid_01",
                title = "How I Use AI to Automate My Entire Workflow in 2026",
                description = "In this video I share the full setup of automations, scripts and AI agents used in my daily tech routine.",
                currentTags = listOf("ai automation", "productivity 2026", "chatgpt coding", "tech tips"),
                thumbnailUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&auto=format&fit=crop&q=80",
                views = "84.2K",
                likes = "5.1K",
                commentsCount = "384",
                publishedDate = "3 days ago",
                ctrPercentage = 4.8
            ),
            YouTubeVideo(
                id = "vid_02",
                title = "The Best Tech Gadgets Nobody is Talking About",
                description = "Reviewing 5 underrated tech gadgets and smart desk accessories that actually improved my productivity.",
                currentTags = listOf("tech review", "desk setup", "gadgets", "unboxing"),
                thumbnailUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=600&auto=format&fit=crop&q=80",
                views = "156.4K",
                likes = "9.8K",
                commentsCount = "720",
                publishedDate = "1 week ago",
                ctrPercentage = 6.2
            ),
            YouTubeVideo(
                id = "vid_03",
                title = "Building a Full Stack Mobile App in 1 Hour (Live Coding)",
                description = "Watch me build a modern Kotlin Jetpack Compose app from scratch with backend REST APIs in under 60 minutes.",
                currentTags = listOf("android dev", "jetpack compose", "kotlin", "full stack"),
                thumbnailUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=600&auto=format&fit=crop&q=80",
                views = "42.9K",
                likes = "3.2K",
                commentsCount = "215",
                publishedDate = "2 weeks ago",
                ctrPercentage = 3.9
            ),
            YouTubeVideo(
                id = "vid_04",
                title = "YouTube Algorithm Secrets: Why Small Channels Explode",
                description = "Deep dive into YouTube recommendation systems, browse features vs suggested videos, and retention graphs.",
                currentTags = listOf("youtube algorithm", "grow on youtube", "analytics breakdown"),
                thumbnailUrl = "https://images.unsplash.com/photo-1611162617474-5b21e879e113?w=600&auto=format&fit=crop&q=80",
                views = "210.5K",
                likes = "14.3K",
                commentsCount = "1.2K",
                publishedDate = "3 weeks ago",
                ctrPercentage = 7.4
            )
        )
    }
}
