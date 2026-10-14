package com.example.ui.screens

import android.app.Activity
import android.app.PictureInPictureParams
import android.net.Uri
import android.os.Build
import android.util.Rational
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.NewsArticle
import com.example.ui.theme.IndoNavy
import com.example.ui.theme.SanhitaBlue
import com.example.ui.viewmodel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeArticle by viewModel.activeVideoArticle.collectAsState()
    val articles by viewModel.articles.collectAsState()

    var isPlaying by remember { mutableStateOf(true) }
    var videoSpeed by remember { mutableStateOf(1.0f) }

    val videoArticle = activeArticle ?: articles.firstOrNull { it.videoUrl != null } ?: articles.first()
    val videoUrl = videoArticle.videoUrl ?: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("video_player_screen")
    ) {
        // Video Player Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .background(Color.Black)
                .testTag("video_player_container")
        ) {
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        setVideoURI(Uri.parse(videoUrl))
                        val mediaController = MediaController(ctx)
                        mediaController.setAnchorView(this)
                        setMediaController(mediaController)
                        setOnPreparedListener { mp ->
                            mp.isLooping = true
                            start()
                            isPlaying = true
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Top overlay bar with PiP button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.TopEnd),
                horizontalArrangement = Arrangement.End
            ) {
                // Picture-in-Picture Button
                IconButton(
                    onClick = {
                        val activity = context as? Activity
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && activity != null) {
                            val params = PictureInPictureParams.Builder()
                                .setAspectRatio(Rational(16, 9))
                                .build()
                            activity.enterPictureInPictureMode(params)
                        }
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .testTag("pip_mode_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureInPictureAlt,
                        contentDescription = "Enter Picture in Picture",
                        tint = Color.White
                    )
                }
            }
        }

        // Video Information and Controls
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "LIVE VIDEO EXPLAINER",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Speed multiplier
                    TextButton(
                        onClick = {
                            videoSpeed = when (videoSpeed) {
                                0.75f -> 1.0f
                                1.0f -> 1.25f
                                1.25f -> 1.5f
                                1.5f -> 2.0f
                                else -> 0.75f
                            }
                        },
                        modifier = Modifier.testTag("video_speed_toggle")
                    ) {
                        Text("${videoSpeed}x", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Native PiP label button
                    FilledTonalButton(
                        onClick = {
                            val activity = context as? Activity
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && activity != null) {
                                val params = PictureInPictureParams.Builder()
                                    .setAspectRatio(Rational(16, 9))
                                    .build()
                                activity.enterPictureInPictureMode(params)
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("enter_pip_action")
                    ) {
                        Icon(Icons.Default.PictureInPicture, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PiP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = videoArticle.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "INDOSANHITA Global Broadcast • ${videoArticle.sourceName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = videoArticle.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Related Video News Bulletins",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // Related Video News List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            val otherVideos = articles.filter { it.id != videoArticle.id }
            items(otherVideos) { article ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openVideoPlayer(article) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircleOutline,
                            contentDescription = null,
                            tint = SanhitaBlue,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = article.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2
                            )
                            Text(
                                text = "${article.category} • ${article.readingTimeMinutes}m",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}
