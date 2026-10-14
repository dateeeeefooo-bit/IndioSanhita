package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.NewsArticle
import com.example.service.AudioPlayerController
import com.example.ui.theme.IndoNavy
import com.example.ui.theme.SanhitaBlue
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBrandBar(
    viewModel: NewsViewModel,
    onOpenSearch: () -> Unit,
    onOpenFileOpener: () -> Unit,
    onOpenVideoPlayer: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val canGoBack = currentScreen != AppScreen.HOME

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.testTag("brand_header")
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_brand_logo),
                    contentDescription = "INDOSANHITA Logo",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "INDO",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "SANHITA",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = SanhitaBlue
                        )
                    }
                    Text(
                        text = "ASCEND WITH EVERY STORY",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        },
        navigationIcon = {
            if (canGoBack) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = onOpenFileOpener,
                modifier = Modifier.testTag("pdf_opener_button")
            ) {
                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = "PDF & Document Opener",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(
                onClick = onOpenVideoPlayer,
                modifier = Modifier.testTag("video_player_button")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "Video News & PiP",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
            IconButton(
                onClick = onOpenSearch,
                modifier = Modifier.testTag("top_search_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
            IconButton(
                onClick = onOpenProfile,
                modifier = Modifier.testTag("top_profile_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile & Settings"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun BottomNavigationBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = Modifier.testTag("bottom_nav_bar")
    ) {
        NavigationBarItem(
            selected = currentScreen == AppScreen.HOME,
            onClick = { onNavigate(AppScreen.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_home")
        )
        NavigationBarItem(
            selected = currentScreen == AppScreen.SEARCH,
            onClick = { onNavigate(AppScreen.SEARCH) },
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            label = { Text("Search", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_search")
        )
        NavigationBarItem(
            selected = currentScreen == AppScreen.AI_CHAT,
            onClick = { onNavigate(AppScreen.AI_CHAT) },
            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI") },
            label = { Text("AI Chat", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_ai")
        )
        NavigationBarItem(
            selected = currentScreen == AppScreen.VERIFY,
            onClick = { onNavigate(AppScreen.VERIFY) },
            icon = { Icon(Icons.Default.FactCheck, contentDescription = "Verify") },
            label = { Text("Verify", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_verify")
        )
        NavigationBarItem(
            selected = currentScreen == AppScreen.SAVED,
            onClick = { onNavigate(AppScreen.SAVED) },
            icon = { Icon(Icons.Default.Bookmark, contentDescription = "Saved") },
            label = { Text("Saved", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_saved")
        )
    }
}

@Composable
fun AudioPlayerBar(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val audioState by AudioPlayerController.state.collectAsState()

    if (audioState.isPlaying || audioState.transcript.isNotBlank()) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag("audio_player_bar"),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Audio playing",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = audioState.articleTitle.ifBlank { "INDOSANHITA Audio" },
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Background AI Spoken Overview • ${audioState.currentSpeed}x",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Speed cycle button
                        TextButton(
                            onClick = {
                                val nextSpeed = when (audioState.currentSpeed) {
                                    0.75f -> 1.0f
                                    1.0f -> 1.25f
                                    1.25f -> 1.5f
                                    1.5f -> 2.0f
                                    else -> 0.75f
                                }
                                viewModel.setAudioSpeed(nextSpeed)
                            },
                            modifier = Modifier.testTag("audio_speed_btn")
                        ) {
                            Text("${audioState.currentSpeed}x", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        IconButton(
                            onClick = { viewModel.toggleAudioPlayback() },
                            modifier = Modifier.testTag("audio_play_pause_btn")
                        ) {
                            Icon(
                                imageVector = if (audioState.isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                contentDescription = if (audioState.isPlaying) "Pause" else "Play",
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = { viewModel.stopAudio() },
                            modifier = Modifier.testTag("audio_close_btn")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close Player", modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsCard(
    article: NewsArticle,
    translatedText: Pair<String, String>?,
    onCardClick: () -> Unit,
    onTranslateClick: () -> Unit,
    onTalkClick: () -> Unit,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayTitle = translatedText?.first ?: article.title
    val displaySummary = translatedText?.second ?: article.summary
    val isTranslated = translatedText != null

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("news_card_${article.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = article.imageUrl,
                    contentDescription = article.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Category Chip Overlay
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = IndoNavy.copy(alpha = 0.85f),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = article.category.uppercase(),
                        color = Color(0xFF60A5FA),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                if (isTranslated) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SanhitaBlue.copy(alpha = 0.9f),
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "TRANSLATED",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Source and timestamp
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = article.sourceName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " • ${article.readingTimeMinutes} min read",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Headline
                Text(
                    text = displayTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Summary
                Text(
                    text = displaySummary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Action Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Talk & Learn More Button
                    FilledTonalButton(
                        onClick = onTalkClick,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("talk_learn_btn_${article.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Talk & Learn", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Translate Button
                        IconButton(
                            onClick = onTranslateClick,
                            modifier = Modifier.testTag("translate_btn_${article.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "Translate",
                                tint = if (isTranslated) SanhitaBlue else MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Bookmark Button
                        IconButton(
                            onClick = onSaveClick,
                            modifier = Modifier.testTag("save_btn_${article.id}")
                        ) {
                            Icon(
                                imageVector = if (article.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Save",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Share Button
                        IconButton(
                            onClick = onShareClick,
                            modifier = Modifier.testTag("share_btn_${article.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
