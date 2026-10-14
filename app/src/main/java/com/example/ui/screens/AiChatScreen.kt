package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.ui.theme.IndoNavy
import com.example.ui.theme.SanhitaBlue
import com.example.ui.viewmodel.NewsViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val messages by viewModel.chatMessages.collectAsState(initial = emptyList())
    val selectedProvider by viewModel.selectedAiProvider.collectAsState()
    val selectedStyle by viewModel.selectedAiStyle.collectAsState()
    val selectedAiLength by viewModel.selectedAiLength.collectAsState()
    val isStreaming by viewModel.isAiStreaming.collectAsState()
    val streamingText by viewModel.aiStreamingText.collectAsState()
    val activeAttachment by viewModel.chatAttachmentName.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var isListeningVoice by remember { mutableStateOf(false) }
    var voiceTranscriptionPreview by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    // Scroll to bottom on new message
    LaunchedEffect(messages.size, streamingText) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size)
        }
    }

    // SpeechRecognizer setup
    val speechRecognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else null
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer?.destroy()
        }
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) { isListeningVoice = true }
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() { isListeningVoice = false }
                override fun onError(error: Int) {
                    isListeningVoice = false
                    Toast.makeText(context, "Voice input completed or cancelled", Toast.LENGTH_SHORT).show()
                }
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        inputText = matches[0]
                        voiceTranscriptionPreview = ""
                    }
                    isListeningVoice = false
                }
                override fun onPartialResults(partialResults: Bundle?) {
                    val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!partial.isNullOrEmpty()) {
                        voiceTranscriptionPreview = partial[0]
                    }
                }
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            speechRecognizer?.startListening(intent)
        } else {
            Toast.makeText(context, "Microphone permission is required for voice query", Toast.LENGTH_SHORT).show()
        }
    }

    // Photo / File Picker for attachments
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.setChatAttachment("Screenshot / Document: ${uri.lastPathSegment ?: "file"}")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("ai_chat_screen")
    ) {
        // Controls Header (Model Selector, Style chips, Length chips)
        Surface(
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                // Provider Selector & Clear Chat Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilterChip(
                            selected = selectedProvider == "INDOSANHITA Intelligence",
                            onClick = { viewModel.setAiProvider("INDOSANHITA Intelligence") },
                            label = { Text("INDOSANHITA Engine", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.testTag("provider_indosanhita")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FilterChip(
                            selected = selectedProvider == "Gemini",
                            onClick = { viewModel.setAiProvider("Gemini") },
                            label = { Text("Gemini 1.5", fontSize = 11.sp) },
                            modifier = Modifier.testTag("provider_gemini")
                        )
                    }

                    IconButton(
                        onClick = { viewModel.clearChat() },
                        modifier = Modifier.size(32.dp).testTag("clear_chat_btn")
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Clear Chat", tint = MaterialTheme.colorScheme.secondary)
                    }
                }

                // Response Style Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val styles = listOf("Quick Answer", "Balanced", "Detailed", "Deep Analysis", "News Expert")
                    styles.forEach { style ->
                        val isSel = style == selectedStyle
                        SuggestionChip(
                            onClick = { viewModel.setAiStyle(style) },
                            label = { Text(style, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (isSel) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                            ),
                            modifier = Modifier.testTag("style_chip_$style")
                        )
                    }
                }

                // Response Length Pills
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text("Length:", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                    listOf("Short", "Medium", "Long").forEach { len ->
                        val isSel = len == selectedAiLength
                        Surface(
                            shape = CircleShape,
                            color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .clickable { viewModel.setAiLength(len) }
                                .padding(2.dp)
                        ) {
                            Text(
                                text = len,
                                fontSize = 10.sp,
                                color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Active Attachment Preview if any
        AnimatedVisibility(visible = activeAttachment != null) {
            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = activeAttachment ?: "",
                            fontSize = 12.sp,
                            maxLines = 1,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(
                        onClick = { viewModel.setChatAttachment(null) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove attachment", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Listening voice preview overlay
        AnimatedVisibility(visible = isListeningVoice) {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Listening...", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(
                            text = voiceTranscriptionPreview.ifBlank { "Speak now..." },
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp
                        )
                    }
                    IconButton(onClick = { speechRecognizer?.stopListening() }) {
                        Icon(Icons.Default.Check, contentDescription = "Done", tint = Color.White)
                    }
                }
            }
        }

        // Messages Conversation List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (messages.isEmpty() && !isStreaming) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = SanhitaBlue
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "INDOSANHITA Intelligence",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ask questions grounded in verified global reporting and real citations.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        // Suggested prompts
                        listOf(
                            "What are the major terms of the New Delhi Clean Energy Pact?",
                            "Explain the NISAR radar mission in simple terms",
                            "How does on-device multimodal AI safeguard user privacy?"
                        ).forEach { suggestion ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        inputText = suggestion
                                        viewModel.sendChatMessage(suggestion)
                                    }
                            ) {
                                Text(
                                    text = suggestion,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            items(messages, key = { it.id }) { message ->
                ChatMessageItem(
                    message = message,
                    onCopy = { clipboardManager.setText(AnnotatedString(message.content)) }
                )
            }

            // Live streaming bubble
            if (isStreaming && streamingText.isNotBlank()) {
                item {
                    ChatMessageItem(
                        message = ChatMessage(
                            id = "streaming_current",
                            role = "assistant",
                            content = streamingText,
                            provider = selectedProvider,
                            style = selectedStyle,
                            length = selectedAiLength,
                            isStreaming = true
                        ),
                        onCopy = {}
                    )
                }
            }
        }

        // Bottom Composer Bar
        Surface(
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Attach file / image button
                IconButton(
                    onClick = { photoPickerLauncher.launch("*/*") },
                    modifier = Modifier.testTag("attach_btn")
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = "Attach image or document")
                }

                // Voice microphone button
                IconButton(
                    onClick = { audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                    modifier = Modifier.testTag("voice_mic_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice input",
                        tint = if (isListeningVoice) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }

                // Input Text Field
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask or analyze stories...", fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(20.dp),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Send Button
                IconButton(
                    onClick = {
                        val prompt = inputText.trim()
                        if (prompt.isNotBlank()) {
                            inputText = ""
                            viewModel.sendChatMessage(prompt)
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .testTag("send_chat_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    onCopy: () -> Unit
) {
    val isUser = message.role == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isUser) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .padding(14.dp)
        ) {
            // Role & Provider Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isUser) "You" else message.provider,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary
                )

                if (!isUser) {
                    Row {
                        IconButton(
                            onClick = onCopy,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            if (message.attachmentName != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isUser) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                ) {
                    Text(
                        text = "📎 ${message.attachmentName}",
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = message.content,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Grounded Citations List
            if (message.citations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "CITATIONS & GROUNDED SOURCES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                message.citations.forEach { cit ->
                    Text(
                        text = "• ${cit.title} (${cit.source})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
