package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.service.AudioPlayerController
import com.example.ui.theme.IndoNavy
import com.example.ui.theme.SanhitaBlue
import com.example.ui.viewmodel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val article = viewModel.selectedArticle.collectAsState().value ?: return
    val textScale by viewModel.articleTextScale.collectAsState()
    val translatedArticles by viewModel.translatedArticles.collectAsState()
    val audioState by AudioPlayerController.state.collectAsState()

    val translatedPair = translatedArticles[article.id]
    val displayTitle = translatedPair?.first ?: article.title
    val displaySummary = translatedPair?.second ?: article.summary
    val isTranslated = translatedPair != null

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .testTag("article_detail_screen")
    ) {
        // Lead Hero Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = article.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Surface(
                color = IndoNavy.copy(alpha = 0.85f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    text = article.imageCredit,
                    color = Color(0xFFAAB4C3),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Column(modifier = Modifier.padding(18.dp)) {
            // Category & Metadata Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = article.category.uppercase(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Text Size Scale Controller
                    IconButton(
                        onClick = { viewModel.setArticleTextScale(textScale - 0.1f) },
                        modifier = Modifier.size(36.dp).testTag("text_decrease_btn")
                    ) {
                        Text("A-", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    IconButton(
                        onClick = { viewModel.setArticleTextScale(textScale + 0.1f) },
                        modifier = Modifier.size(36.dp).testTag("text_increase_btn")
                    ) {
                        Text("A+", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    // Translate toggle
                    IconButton(
                        onClick = { viewModel.toggleArticleTranslation(article) },
                        modifier = Modifier.size(36.dp).testTag("detail_translate_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Translate",
                            tint = if (isTranslated) SanhitaBlue else MaterialTheme.colorScheme.secondary
                        )
                    }

                    // Save Bookmark
                    IconButton(
                        onClick = { viewModel.toggleBookmark(article) },
                        modifier = Modifier.size(36.dp).testTag("detail_save_btn")
                    ) {
                        Icon(
                            imageVector = if (article.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Native Share
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, article.title)
                                putExtra(Intent.EXTRA_TEXT, "${article.title}\n\n${article.sourceUrl}")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share News"))
                        },
                        modifier = Modifier.size(36.dp).testTag("detail_share_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Headline
            Text(
                text = displayTitle,
                fontSize = (22 * textScale).sp,
                fontWeight = FontWeight.Bold,
                lineHeight = (28 * textScale).sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Source Provenance and Timestamps
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Source: ${article.sourceName}",
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

            Spacer(modifier = Modifier.height(14.dp))

            // AI Speak Overview Card with Audio controls
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_overview_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = SanhitaBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI SPOKEN OVERVIEW",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = SanhitaBlue
                            )
                        }

                        Button(
                            onClick = {
                                if (audioState.isPlaying) {
                                    viewModel.toggleAudioPlayback()
                                } else {
                                    viewModel.startAudioOverview(article, 1.0f)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("listen_btn")
                        ) {
                            Icon(
                                imageVector = if (audioState.isPlaying) Icons.Default.Pause else Icons.Default.VolumeUp,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (audioState.isPlaying) "Pause" else "Listen",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = article.aiOverview,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Marked AI-generated summary • Background audio enabled",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Body Content
            Text(
                text = article.content,
                fontSize = (16 * textScale).sp,
                lineHeight = (24 * textScale).sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(20.dp))

            // What We Know / What Remains Unclear
            if (article.whatIsClear.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "WHAT IS VERIFIED",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF147D64)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        article.whatIsClear.forEach { item ->
                            Text(
                                text = "• $item",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }

                        if (article.whatRemainsUnclear.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "WHAT REMAINS UNCLEAR",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB7791F)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            article.whatRemainsUnclear.forEach { item ->
                                Text(
                                    text = "• $item",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Original Publisher Link & Talk & Learn
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(article.sourceUrl))
                        context.startActivity(browserIntent)
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("visit_publisher_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Visit Publisher", fontSize = 12.sp)
                }

                Button(
                    onClick = { viewModel.talkAndLearnMore(article) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("talk_learn_detail_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Talk & Learn", fontSize = 12.sp)
                }
            }

            // Video Player Link if video available
            if (article.videoUrl != null) {
                Spacer(modifier = Modifier.height(10.dp))
                FilledTonalButton(
                    onClick = { viewModel.openVideoPlayer(article) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("watch_video_btn")
                ) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Watch Video Explainer & PiP Mode", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
