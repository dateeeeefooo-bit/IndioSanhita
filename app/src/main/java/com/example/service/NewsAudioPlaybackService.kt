package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

object AudioPlayerController {
    data class AudioState(
        val isPlaying: Boolean = false,
        val articleTitle: String = "",
        val source: String = "",
        val transcript: String = "",
        val currentSpeed: Float = 1.0f,
        val languageCode: String = "en",
        val progressPercent: Float = 0f
    )

    private val _state = MutableStateFlow(AudioState())
    val state = _state.asStateFlow()

    fun updateState(transform: (AudioState) -> AudioState) {
        _state.value = transform(_state.value)
    }
}

class NewsAudioPlaybackService : Service(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    companion object {
        const val CHANNEL_ID = "indosanhita_audio_channel"
        const val NOTIFICATION_ID = 2001

        const val ACTION_START_PLAY = "action_start_play"
        const val ACTION_PAUSE = "action_pause"
        const val ACTION_RESUME = "action_resume"
        const val ACTION_STOP = "action_stop"
        const val ACTION_SET_SPEED = "action_set_speed"

        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_SOURCE = "extra_source"
        const val EXTRA_TEXT = "extra_text"
        const val EXTRA_LANG = "extra_lang"
        const val EXTRA_SPEED = "extra_speed"

        fun startAudio(
            context: Context,
            title: String,
            source: String,
            textToSpeak: String,
            lang: String = "en",
            speed: Float = 1.0f
        ) {
            val intent = Intent(context, NewsAudioPlaybackService::class.java).apply {
                action = ACTION_START_PLAY
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_SOURCE, source)
                putExtra(EXTRA_TEXT, textToSpeak)
                putExtra(EXTRA_LANG, lang)
                putExtra(EXTRA_SPEED, speed)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun togglePlayPause(context: Context) {
            val isPlaying = AudioPlayerController.state.value.isPlaying
            val action = if (isPlaying) ACTION_PAUSE else ACTION_RESUME
            val intent = Intent(context, NewsAudioPlaybackService::class.java).apply {
                this.action = action
            }
            context.startService(intent)
        }

        fun setSpeed(context: Context, speed: Float) {
            val intent = Intent(context, NewsAudioPlaybackService::class.java).apply {
                action = ACTION_SET_SPEED
                putExtra(EXTRA_SPEED, speed)
            }
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, NewsAudioPlaybackService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    AudioPlayerController.updateState { it.copy(isPlaying = true) }
                    updateNotification(AudioPlayerController.state.value.articleTitle, AudioPlayerController.state.value.source, isPlaying = true)
                }

                override fun onDone(utteranceId: String?) {
                    AudioPlayerController.updateState { it.copy(isPlaying = false, progressPercent = 1.0f) }
                    updateNotification(AudioPlayerController.state.value.articleTitle, AudioPlayerController.state.value.source, isPlaying = false)
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    AudioPlayerController.updateState { it.copy(isPlaying = false) }
                }
            })

            // If start pending, execute
            val cur = AudioPlayerController.state.value
            if (cur.transcript.isNotBlank() && cur.isPlaying) {
                speakCurrentTranscript()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: return START_NOT_STICKY

        when (action) {
            ACTION_START_PLAY -> {
                val title = intent.getStringExtra(EXTRA_TITLE) ?: "INDOSANHITA Audio"
                val source = intent.getStringExtra(EXTRA_SOURCE) ?: "Global News"
                val text = intent.getStringExtra(EXTRA_TEXT) ?: ""
                val lang = intent.getStringExtra(EXTRA_LANG) ?: "en"
                val speed = intent.getFloatExtra(EXTRA_SPEED, 1.0f)

                AudioPlayerController.updateState {
                    it.copy(
                        isPlaying = true,
                        articleTitle = title,
                        source = source,
                        transcript = text,
                        currentSpeed = speed,
                        languageCode = lang,
                        progressPercent = 0f
                    )
                }

                val notification = buildNotification(title, source, isPlaying = true)
                startForeground(NOTIFICATION_ID, notification)

                if (isTtsReady) {
                    speakCurrentTranscript()
                }
            }

            ACTION_PAUSE -> {
                tts?.stop()
                AudioPlayerController.updateState { it.copy(isPlaying = false) }
                val cur = AudioPlayerController.state.value
                updateNotification(cur.articleTitle, cur.source, isPlaying = false)
            }

            ACTION_RESUME -> {
                AudioPlayerController.updateState { it.copy(isPlaying = true) }
                speakCurrentTranscript()
                val cur = AudioPlayerController.state.value
                updateNotification(cur.articleTitle, cur.source, isPlaying = true)
            }

            ACTION_SET_SPEED -> {
                val newSpeed = intent.getFloatExtra(EXTRA_SPEED, 1.0f)
                AudioPlayerController.updateState { it.copy(currentSpeed = newSpeed) }
                tts?.setSpeechRate(newSpeed)
            }

            ACTION_STOP -> {
                tts?.stop()
                AudioPlayerController.updateState { it.copy(isPlaying = false) }
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    private fun speakCurrentTranscript() {
        val state = AudioPlayerController.state.value
        val locale = when (state.languageCode) {
            "hi" -> Locale("hi", "IN")
            "es" -> Locale("es", "ES")
            "fr" -> Locale.FRENCH
            "de" -> Locale.GERMAN
            else -> Locale.US
        }
        tts?.language = locale
        tts?.setSpeechRate(state.currentSpeed)
        tts?.speak(state.transcript, TextToSpeech.QUEUE_FLUSH, null, "news_utterance_1")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "INDOSANHITA Audio Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Background AI Spoken Overview Controls"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(title: String, source: String, isPlaying: Boolean): Notification {
        val tapIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            this, 0, tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Play/Pause Action Intent
        val playPauseIntent = Intent(this, NewsAudioPlaybackService::class.java).apply {
            action = if (isPlaying) ACTION_PAUSE else ACTION_RESUME
        }
        val playPausePendingIntent = PendingIntent.getService(
            this, 1, playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Stop Action Intent
        val stopIntent = Intent(this, NewsAudioPlaybackService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 2, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseIcon = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        val playPauseTitle = if (isPlaying) "Pause" else "Play"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("INDOSANHITA: $title")
            .setContentText("$source • AI Audio Overview")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(contentPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(isPlaying)
            .addAction(playPauseIcon, playPauseTitle, playPausePendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent)
            .build()
    }

    private fun updateNotification(title: String, source: String, isPlaying: Boolean) {
        val notification = buildNotification(title, source, isPlaying)
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
