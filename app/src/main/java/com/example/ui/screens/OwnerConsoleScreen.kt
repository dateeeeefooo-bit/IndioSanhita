package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RegisteredUserInfo
import com.example.data.model.UserRole
import com.example.data.repository.GeminiApiClient
import com.example.ui.theme.IndoNavy
import com.example.ui.theme.SanhitaBlue
import com.example.ui.viewmodel.NewsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerConsoleScreen(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val auditLogs by viewModel.authRepo.getAuditLogs().collectAsState(initial = emptyList())
    val healthList = remember { viewModel.authRepo.getServiceHealthList() }
    val registeredUsers: List<RegisteredUserInfo> = remember { viewModel.authRepo.getRegisteredUsers() }

    var selectedTab by remember { mutableStateOf(0) }

    // Feature Flags State
    var flagBackgroundAudio by remember { mutableStateOf(true) }
    var flagLiveVoice by remember { mutableStateOf(true) }
    var flagImageGen by remember { mutableStateOf(true) }
    var flagNativeWidget by remember { mutableStateOf(true) }

    // Broadcast Dispatcher State
    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastBody by remember { mutableStateOf("") }
    var broadcastCategory by remember { mutableStateOf("BREAKING") } // "BREAKING", "AI_SUGGESTION", "EMAIL_DIGEST"

    val isGeminiLive = remember { GeminiApiClient.isRealKeyConfigured() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("owner_console_screen")
    ) {
        // Master Header
        Surface(
            color = IndoNavy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "INDOSANHITA OWNER CONSOLE",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Authorized Session: Searkkk@gmail.com",
                                color = Color(0xFFAAB4C3),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Surface(
                        color = Color(0xFF147D64),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "ONLINE",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Section Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            edgePadding = 12.dp
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Health", fontSize = 12.sp) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("AI Telemetry", fontSize = 12.sp) })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Users (${registeredUsers.size})", fontSize = 12.sp) })
            Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Broadcast Alerts", fontSize = 12.sp) })
            Tab(selected = selectedTab == 4, onClick = { selectedTab = 4 }, text = { Text("Devices & TV", fontSize = 12.sp) })
            Tab(selected = selectedTab == 5, onClick = { selectedTab = 5 }, text = { Text("Flags", fontSize = 12.sp) })
            Tab(selected = selectedTab == 6, onClick = { selectedTab = 6 }, text = { Text("Audit (${auditLogs.size})", fontSize = 12.sp) })
        }

        when (selectedTab) {
            0 -> {
                // Service Health Cards
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(healthList) { health ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = health.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = health.details,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = "Latency: ${health.latencyMs}ms",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Surface(
                                    color = Color(0xFF147D64).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = health.status,
                                        color = Color(0xFF147D64),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // AI Providers Configuration & Real-Time Stats
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Primary Model: Gemini 3.5 Flash",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    color = if (isGeminiLive) Color(0xFF147D64) else SanhitaBlue,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = if (isGeminiLive) "LIVE API" else "HYBRID MODE",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Direct REST client configured with 60s timeouts & grounded intelligence fallback.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Active Key: •••••••••••••••• (Injected via AI Studio Secrets)",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "INDOSANHITA Intelligence Engine",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• Multi-Perspective Analysis: Quick Answer, Balanced, Detailed, Deep Analysis, News Expert\n• C2PA Multimodal Fact Checking\n• Multilingual Instant Translation (11 Global Languages)\n• Background AI Audio Overview Synthesizer",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            2 -> {
                // Registered Users & Account Activity
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    item {
                        Text(
                            text = "All Registered Users & Active Client Devices",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    items(registeredUsers) { regUser ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = regUser.displayName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (regUser.role == UserRole.OWNER) SanhitaBlue.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Text(
                                                text = regUser.status,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (regUser.role == UserRole.OWNER) SanhitaBlue else MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(text = regUser.email, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                                    Text(text = "Device: ${regUser.deviceType} • Last active: ${regUser.lastActive}", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "${regUser.coinBalance} Coins", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFD97706))
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // Broadcast Alerts & Mails Dispatcher
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Broadcast News, AI Suggestions & Mails",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Dispatches push notifications and simulated inbox emails directly to all registered readers on Mobile, TV, Laptop and Browser.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Text("Notification Type:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("BREAKING" to "Breaking News", "AI_SUGGESTION" to "AI Suggestion", "EMAIL_DIGEST" to "Mail Digest").forEach { (cat, label) ->
                            FilterChip(
                                selected = broadcastCategory == cat,
                                onClick = { broadcastCategory = cat },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = broadcastTitle,
                        onValueChange = { broadcastTitle = it },
                        label = { Text("Alert / Email Headline") },
                        placeholder = { Text("e.g. Breakthrough in Global Semiconductor Corridor") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = broadcastBody,
                        onValueChange = { broadcastBody = it },
                        label = { Text("Notification Content / Digest Body") },
                        placeholder = { Text("e.g. New bilateral corridor agreement signed with $10B allocation.") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Button(
                        onClick = {
                            if (broadcastTitle.isNotBlank() && broadcastBody.isNotBlank()) {
                                viewModel.broadcastNotification(broadcastTitle, broadcastBody, broadcastCategory)
                                Toast.makeText(context, "Broadcast notification dispatched to all devices!", Toast.LENGTH_SHORT).show()
                                broadcastTitle = ""
                                broadcastBody = ""
                            } else {
                                Toast.makeText(context, "Please enter title and content.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dispatch to All Readers")
                    }
                }
            }

            4 -> {
                // Device Compatibility & Telemetry Matrix
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Multi-Device Compatibility & Form Factors",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "INDOSANHITA APK is fully adaptive across standard handhelds, large screen foldables, Android TV, and desktop browser environments.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    val deviceStats = listOf(
                        Triple("Android Mobile (APKs)", "62% Active Sessions", "Portrait 420dp • Single-hand edge-to-edge"),
                        Triple("Android Tablet & Foldables", "18% Active Sessions", "Landscape 680dp max-width centered scaffold"),
                        Triple("Android TV & DeX", "12% Active Sessions", "16:9 PiP Video player & audio controller support"),
                        Triple("Browser / Web Streaming", "8% Active Sessions", "Full keyboard/mouse & responsive layout engine")
                    )

                    deviceStats.forEach { (device, pct, desc) ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = device, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Surface(color = SanhitaBlue.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                        Text(text = pct, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SanhitaBlue, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
            }

            5 -> {
                // Feature Flags
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Real-Time System Feature Toggles",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text("Background Audio Foreground Service", fontSize = 13.sp)
                            Switch(checked = flagBackgroundAudio, onCheckedChange = { flagBackgroundAudio = it })
                        }
                    }

                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text("Live Speech-to-Text Voice Input", fontSize = 13.sp)
                            Switch(checked = flagLiveVoice, onCheckedChange = { flagLiveVoice = it })
                        }
                    }

                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text("AI Concept Image Generation", fontSize = 13.sp)
                            Switch(checked = flagImageGen, onCheckedChange = { flagImageGen = it })
                        }
                    }

                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text("Native Android AppWidget Sync", fontSize = 13.sp)
                            Switch(checked = flagNativeWidget, onCheckedChange = { flagNativeWidget = it })
                        }
                    }
                }
            }

            6 -> {
                // Audit Logs List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(auditLogs) { log ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = log.action, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SanhitaBlue)
                                    val dateStr = SimpleDateFormat("dd MMM, HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
                                    Text(text = dateStr, fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Actor: ${log.actor} • Target: ${log.target}", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                                Text(text = "Reason: ${log.reason}", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
