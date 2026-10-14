package com.example
import android.app.PictureInPictureParams
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.AudioPlayerBar
import com.example.ui.components.BottomNavigationBar
import com.example.ui.components.TopBrandBar
import com.example.ui.screens.*
import com.example.ui.theme.IndosanhitaTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NewsViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: NewsViewModel by viewModels()
    private val isInPipState = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIncomingIntent(intent)

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val isDark = when (themeMode) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }

            IndosanhitaTheme(darkTheme = isDark) {
                val currentScreen by viewModel.currentScreen.collectAsState()

                // Hardware & System Back Gesture Handler
                BackHandler(enabled = currentScreen != AppScreen.HOME) {
                    viewModel.navigateBack()
                }

                val isInPip by isInPipState

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                            .widthIn(max = 680.dp)
                            .testTag("main_scaffold"),
                        topBar = {
                            if (!isInPip) {
                                TopBrandBar(
                                    viewModel = viewModel,
                                    onOpenSearch = { viewModel.navigateTo(AppScreen.SEARCH) },
                                    onOpenFileOpener = { viewModel.openDocumentViewer() },
                                    onOpenVideoPlayer = { viewModel.navigateTo(AppScreen.VIDEO_PLAYER) },
                                    onOpenProfile = { viewModel.navigateTo(AppScreen.PROFILE) }
                                )
                            }
                        },
                        bottomBar = {
                            if (!isInPip) {
                                BottomNavigationBar(
                                    currentScreen = currentScreen,
                                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(if (isInPip) PaddingValues(0.dp) else innerPadding)
                        ) {
                            // Main View Router
                            when (currentScreen) {
                                AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                                AppScreen.SEARCH -> SearchScreen(viewModel = viewModel)
                                AppScreen.ARTICLE_DETAIL -> ArticleDetailScreen(viewModel = viewModel)
                                AppScreen.AI_CHAT -> AiChatScreen(viewModel = viewModel)
                                AppScreen.VERIFY -> VerifyScreen(viewModel = viewModel)
                                AppScreen.DOCUMENT_VIEWER -> DocumentViewerScreen(viewModel = viewModel)
                                AppScreen.VIDEO_PLAYER -> VideoPlayerScreen(viewModel = viewModel)
                                AppScreen.SAVED -> SavedScreen(viewModel = viewModel)
                                AppScreen.PROFILE -> ProfileScreen(viewModel = viewModel)
                                AppScreen.OWNER_CONSOLE -> OwnerConsoleScreen(viewModel = viewModel)
                            }

                            // Floating Persistent Audio Player Bar (only in normal mode)
                            if (!isInPip) {
                                AudioPlayerBar(
                                    viewModel = viewModel,
                                    modifier = Modifier.align(Alignment.BottomCenter)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val isVideo = viewModel.currentScreen.value == AppScreen.VIDEO_PLAYER
            if (isVideo) {
                try {
                    val params = PictureInPictureParams.Builder()
                        .setAspectRatio(Rational(16, 9))
                        .build()
                    enterPictureInPictureMode(params)
                } catch (_: Exception) {
                }
            }
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        isInPipState.value = isInPictureInPictureMode
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return

        if (intent.action == Intent.ACTION_SEND) {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (!sharedText.isNullOrBlank()) {
                viewModel.navigateTo(AppScreen.VERIFY)
                viewModel.runVerification(sharedText, "text")
            }
        } else if (intent.getBooleanExtra("EXTRA_WIDGET_STORY_CLICK", false)) {
            viewModel.navigateTo(AppScreen.HOME)
        }
    }
}
