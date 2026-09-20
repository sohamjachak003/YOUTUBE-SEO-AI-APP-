package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.AiSeoResult
import com.example.model.ChannelProfile
import com.example.model.TitleOption
import com.example.model.YouTubeVideo
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun YouTubeDashboardScreen(
    viewModel: YouTubeSeoViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(YoutubeRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "YouTube Icon",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "YouTube SEO AI",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Personal AI Team Member",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                            )
                        }
                    }
                },
                actions = {
                    if (uiState is UiState.Connected) {
                        IconButton(
                            onClick = { viewModel.disconnect() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Disconnect",
                                tint = TextSecondary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is UiState.Disconnected -> {
                    LandingConnectView(
                        onConnectClicked = { viewModel.connectYouTubeOAuth() }
                    )
                }
                is UiState.Connecting -> {
                    ConnectingLoadingView()
                }
                is UiState.Connected -> {
                    ConnectedDashboardContent(
                        state = state,
                        onTabSelected = { viewModel.setActiveTab(it) },
                        onVideoSelected = { viewModel.selectVideo(it) },
                        onReOptimize = { state.selectedVideo?.let { viewModel.optimizeVideo(it) } }
                    )
                }
            }
        }
    }
}

@Composable
fun LandingConnectView(
    onConnectClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(YoutubeRedLight, YoutubeRedDark)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = "AI Co-pilot",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Automate Your YouTube SEO",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Connect your channel with Google OAuth 2.0. Your 24/7 AI team member monitors CTR, rewrites titles, engineers rich descriptions, and drafts community posts.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondary,
                lineHeight = 20.sp
            ),
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Security / Scopes Badge
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Security",
                        tint = AccentGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Secure Google OAuth 2.0 Integration",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = AccentGreen
                        )
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• youtube.readonly (Access channel metadata & comments)\n• youtube.force-ssl (Manage video SEO & descriptions)\n• Encrypted session token with automatic refresh",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onConnectClicked,
            colors = ButtonDefaults.buttonColors(containerColor = YoutubeRed),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Connect YouTube Channel",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
fun ConnectingLoadingView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = YoutubeRed)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Authorizing with Google OAuth...",
            style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
        )
        Text(
            text = "Exchanging token & syncing channel metadata",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )
    }
}

@Composable
fun ConnectedDashboardContent(
    state: UiState.Connected,
    onTabSelected: (DashboardTab) -> Unit,
    onVideoSelected: (YouTubeVideo) -> Unit,
    onReOptimize: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Channel Banner
        ChannelHeaderBar(profile = state.profile)

        // Navigation Tabs
        ScrollableTabRow(
            selectedTabIndex = state.activeTab.ordinal,
            containerColor = DarkSurface,
            contentColor = YoutubeRed,
            edgePadding = 16.dp,
            divider = {}
        ) {
            DashboardTab.values().forEach { tab ->
                Tab(
                    selected = state.activeTab == tab,
                    onClick = { onTabSelected(tab) },
                    text = {
                        Text(
                            text = tab.label,
                            color = if (state.activeTab == tab) YoutubeRed else TextSecondary,
                            fontWeight = if (state.activeTab == tab) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Active Tab Screen Content
        Box(modifier = Modifier.weight(1f)) {
            when (state.activeTab) {
                DashboardTab.VIDEOS -> {
                    VideosListView(
                        videos = state.videos,
                        selectedVideo = state.selectedVideo,
                        onVideoSelected = onVideoSelected
                    )
                }
                DashboardTab.SEO_STUDIO -> {
                    SeoStudioView(
                        video = state.selectedVideo,
                        seoResult = state.seoResult,
                        isOptimizing = state.isOptimizing,
                        onReOptimize = onReOptimize
                    )
                }
                DashboardTab.AI_TEAM -> {
                    AiTeamManagerView(
                        profile = state.profile,
                        seoResult = state.seoResult
                    )
                }
                DashboardTab.STRATEGY -> {
                    StrategyReportsView(
                        profile = state.profile,
                        seoResult = state.seoResult
                    )
                }
            }
        }
    }
}

@Composable
fun ChannelHeaderBar(profile: ChannelProfile) {
    Card(
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = profile.avatarUrl,
                contentDescription = profile.name,
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .border(2.dp, YoutubeRed, CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = profile.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified Channel",
                        tint = AccentCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = "${profile.handle} • ${profile.niche}",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatPill(label = "Subs", value = profile.subscriberCount)
                    StatPill(label = "Views", value = profile.totalViews)
                    StatPill(label = "Videos", value = profile.videoCount.toString())
                }
            }
        }
    }
}

@Composable
fun StatPill(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
    }
}

@Composable
fun VideosListView(
    videos: List<YouTubeVideo>,
    selectedVideo: YouTubeVideo?,
    onVideoSelected: (YouTubeVideo) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Recent Channel Uploads",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            )
        }

        items(videos) { video ->
            val isSelected = video.id == selectedVideo?.id
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) DarkSurfaceVariant else DarkSurface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) YoutubeRed else DarkBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onVideoSelected(video) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = video.thumbnailUrl,
                        contentDescription = video.title,
                        modifier = Modifier
                            .width(100.dp)
                            .height(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${video.views} views",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                            )
                            Text(
                                text = "${video.ctrPercentage}% CTR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (video.ctrPercentage > 5.0) AccentGreen else AccentAmber,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = video.publishedDate,
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Select",
                        tint = if (isSelected) YoutubeRed else TextMuted
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SeoStudioView(
    video: YouTubeVideo?,
    seoResult: AiSeoResult?,
    isOptimizing: Boolean,
    onReOptimize: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    if (video == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Select a video from the videos tab to analyze SEO", color = TextSecondary)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active video banner
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Currently Auditing:",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                        )
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Button(
                        onClick = onReOptimize,
                        enabled = !isOptimizing,
                        colors = ButtonDefaults.buttonColors(containerColor = YoutubeRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isOptimizing) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Re-Run AI")
                        }
                    }
                }
            }
        }

        if (seoResult != null) {
            // High-CTR Titles
            item {
                Text(
                    text = "High-CTR Title Proposals (Mobile Optimized < 60 chars)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan
                    )
                )
            }

            items(seoResult.highCtrTitles) { titleOpt ->
                TitleOptionCard(
                    option = titleOpt,
                    onCopy = {
                        clipboardManager.setText(AnnotatedString(titleOpt.title))
                        Toast.makeText(context, "Title copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // Description Box
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Optimized Rich Description",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            TextButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(seoResult.optimizedDescription))
                                    Toast.makeText(context, "Description copied!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy", color = AccentCyan)
                            }
                        }
                        Text(
                            text = seoResult.optimizedDescription,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }

            // Trending Tags
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Target High-Intent Tags",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            TextButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(seoResult.tagsCsv))
                                    Toast.makeText(context, "CSV tags copied for YouTube Studio!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy CSV", color = AccentCyan)
                            }
                        }

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            seoResult.trendingTags.forEach { tag ->
                                TagChip(tag = tag)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TitleOptionCard(
    option: TitleOption,
    onCopy: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = option.strategy,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AccentCyan,
                        fontWeight = FontWeight.Bold
                    )
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Pred. CTR: ${option.predictedCTR}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AccentGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy title",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = option.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = option.reason,
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )
        }
    }
}

@Composable
fun TagChip(tag: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = "#$tag",
            style = MaterialTheme.typography.labelSmall.copy(color = AccentCyan)
        )
    }
}

@Composable
fun AiTeamManagerView(
    profile: ChannelProfile,
    seoResult: AiSeoResult?
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Proactive AI Channel Manager",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = "Automated engagement drafts and community building suggestions",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )
        }

        // Pinned Comment Recommendation
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PushPin, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pinned Comment Hook", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                        }
                        TextButton(onClick = {
                            val text = seoResult?.pinnedCommentPitch ?: ""
                            clipboardManager.setText(AnnotatedString(text))
                            Toast.makeText(context, "Pinned comment copied!", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("Copy", color = AccentCyan)
                        }
                    }
                    Text(
                        text = seoResult?.pinnedCommentPitch ?: "Analyzing video discussion velocity...",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, lineHeight = 20.sp)
                    )
                }
            }
        }

        // Community Tab Draft
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Forum, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Community Tab Post & Poll Idea", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                        }
                        TextButton(onClick = {
                            val text = seoResult?.communityPostDraft ?: ""
                            clipboardManager.setText(AnnotatedString(text))
                            Toast.makeText(context, "Community post copied!", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("Copy", color = AccentCyan)
                        }
                    }
                    Text(
                        text = seoResult?.communityPostDraft ?: "Formulating poll and update proposal...",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, lineHeight = 20.sp)
                    )
                }
            }
        }

        // Top Comments Replier
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Automated Comment Response Framework",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "The AI Team Member responds within 15 minutes of user comments to signal active engagement to YouTube's recommendation neural network, favoring open-ended follow-up questions.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }
        }
    }
}

@Composable
fun StrategyReportsView(
    profile: ChannelProfile,
    seoResult: AiSeoResult?
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Weekly Channel Strategy Report",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = "AI-generated channel teardown and algorithmic growth roadmap",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Insights, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Audience Retention & CTR Audit",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = seoResult?.weeklyStrategyTip ?: "Gathering video retention curves and click attribution data...",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, lineHeight = 20.sp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = seoResult?.retentionHeuristics ?: "Evaluating title-thumbnail congruence...",
                        style = MaterialTheme.typography.bodySmall.copy(color = AccentCyan, lineHeight = 18.sp)
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Next Best Upload Recommendations",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. 'Why Everyone is Wrong About AI Code Editors' (Estimated Reach: High)\n2. '5 YouTube Studio Settings Every Creator Must Change' (Search Volume: 48K/mo)\n3. 'The 2026 YouTube Monetization Survival Guide' (High Retention Intent)",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 22.sp)
                    )
                }
            }
        }
    }
}
