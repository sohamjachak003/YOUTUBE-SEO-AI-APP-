package com.example.model

data class ChannelProfile(
    val id: String,
    val name: String,
    val handle: String,
    val avatarUrl: String,
    val subscriberCount: String,
    val totalViews: String,
    val videoCount: Int,
    val niche: String
)

data class YouTubeVideo(
    val id: String,
    val title: String,
    val description: String,
    val currentTags: List<String>,
    val thumbnailUrl: String,
    val views: String,
    val likes: String,
    val commentsCount: String,
    val publishedDate: String,
    val ctrPercentage: Double = 4.2
)

data class TitleOption(
    val title: String,
    val strategy: String,
    val predictedCTR: String,
    val reason: String
)

data class AiSeoResult(
    val videoId: String,
    val highCtrTitles: List<TitleOption>,
    val optimizedDescription: String,
    val trendingTags: List<String>,
    val tagsCsv: String,
    val pinnedCommentPitch: String,
    val communityPostDraft: String,
    val weeklyStrategyTip: String,
    val retentionHeuristics: String
)
