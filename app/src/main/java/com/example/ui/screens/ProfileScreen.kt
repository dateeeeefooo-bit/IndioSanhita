package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.IndoNavy
import com.example.ui.theme.SanhitaBlue
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NewsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsState()
    val coinBalance by viewModel.coinBalance.collectAsState()
    val transactions by viewModel.rewardTransactions.collectAsState(initial = emptyList())
    val themeMode by viewModel.themeMode.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val lastOtp by viewModel.authRepo.lastDispatchedOtp.collectAsState()

    var showOtpDialog by remember { mutableStateOf(false) }
    var showOwnerLoginDialog by remember { mutableStateOf(false) }
    var showImageGenDialog by remember { mutableStateOf(false) }

    // OTP dialog states
    var otpEmailInput by remember { mutableStateOf("reader@indosanhita.com") }
    var otpCodeInput by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }

    // Owner dialog states
    var ownerEmailInput by remember { mutableStateOf("Searkkk@gmail.com") }
    var ownerPasswordInput by remember { mutableStateOf("Krieshna_14") }

    // Image gen states
    var imagePromptInput by remember { mutableStateOf("") }
    var isGeneratingImage by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("profile_screen")
    ) {
        // User Profile Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (user.role == UserRole.OWNER) SanhitaBlue else MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (user.role == UserRole.OWNER) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                        contentDescription = null,
                        tint = if (user.role == UserRole.OWNER) Color.White else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (user.role == UserRole.OWNER) SanhitaBlue.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = if (user.role == UserRole.OWNER) "OWNER / ADMIN" else "READER",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (user.role == UserRole.OWNER) SanhitaBlue else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = user.email.ifBlank { "reader@indosanhita.com" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                if (!user.isAuthenticated || user.role != UserRole.OWNER) {
                    TextButton(onClick = { showOtpDialog = true }) {
                        Text("Sign In", fontWeight = FontWeight.Bold)
                    }
                } else {
                    IconButton(onClick = { viewModel.signOut() }) {
                        Icon(Icons.Default.Logout, contentDescription = "Sign Out", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reading Coins & Rewards Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("rewards_card")
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = Color(0xFFEAB308),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Reading Coins",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "$coinBalance Coins",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Button(
                        onClick = { showImageGenDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SanhitaBlue),
                        modifier = Modifier.testTag("ai_illustrator_btn")
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI Studio (10c)", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Daily Cap: 50 coins", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                    Text("4 Day Reading Streak 🔥", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Owner Console Access Card (Protected)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
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
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = SanhitaBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Private Owner Console",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Provider status, AI telemetry, audit log & system health",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (user.role == UserRole.OWNER) {
                                viewModel.navigateTo(AppScreen.OWNER_CONSOLE)
                            } else {
                                showOwnerLoginDialog = true
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("open_owner_console_btn")
                    ) {
                        Text(if (user.role == UserRole.OWNER) "Open" else "Authenticate", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Settings Section
        Text(
            text = "App & Reading Preferences",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Theme Mode Selector
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(14.dp)
            ) {
                Text("Appearance Theme", fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("system", "light", "dark").forEach { mode ->
                        val isSel = mode == themeMode
                        FilterChip(
                            selected = isSel,
                            onClick = { viewModel.setThemeMode(mode) },
                            label = { Text(mode.replaceFirstChar { it.uppercase() }, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Language Selector
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(14.dp)
            ) {
                Text("Content Language", fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("en" to "English", "hi" to "हिन्दी", "bn" to "বাংলা", "es" to "Español").forEach { (code, label) ->
                        val isSel = code == appLanguage
                        FilterChip(
                            selected = isSel,
                            onClick = { viewModel.setAppLanguage(code) },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Ledger History
        Text(
            text = "Reward Ledger & Transactions",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        transactions.take(5).forEach { tx ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Column {
                        Text(text = tx.description, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        val dateStr = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(tx.timestamp))
                        Text(text = "$dateStr • ${tx.eventType}", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
                    }
                    Text(
                        text = if (tx.amount > 0) "+${tx.amount}" else "${tx.amount}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (tx.amount > 0) Color(0xFF147D64) else Color(0xFFB42318)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Notifications & Mails Section
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = SanhitaBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "News Notifications & Mails",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SanhitaBlue.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "${notifications.size} Updates",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SanhitaBlue,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (notifications.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No pending notifications. Breaking news and AI briefs will appear here.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(14.dp)
                )
            }
        } else {
            notifications.forEach { notif ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = when (notif.category) {
                                    "BREAKING" -> Color(0xFFEF4444).copy(alpha = 0.15f)
                                    "AI_SUGGESTION" -> SanhitaBlue.copy(alpha = 0.15f)
                                    else -> Color(0xFF147D64).copy(alpha = 0.15f)
                                }
                            ) {
                                Text(
                                    text = notif.category.replace("_", " "),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (notif.category) {
                                        "BREAKING" -> Color(0xFFEF4444)
                                        "AI_SUGGESTION" -> SanhitaBlue
                                        else -> Color(0xFF147D64)
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            IconButton(
                                onClick = { viewModel.dismissNotification(notif.id) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = notif.body, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    // OTP Authentication Dialog
    if (showOtpDialog) {
        AlertDialog(
            onDismissRequest = { showOtpDialog = false },
            title = { Text("Reader Sign-Up & OTP Verification") },
            text = {
                Column {
                    Text(
                        text = "Enter your email. A 6-digit OTP passcode will be generated for instant verification.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = otpEmailInput,
                        onValueChange = { otpEmailInput = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (isOtpSent) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SanhitaBlue.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Simulated SMS / Mail Dispatch:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SanhitaBlue
                                )
                                Text(
                                    text = "Your OTP is: ${lastOtp ?: "123456"}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = otpCodeInput,
                            onValueChange = { otpCodeInput = it },
                            label = { Text("Enter 6-Digit Passcode") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FilledTonalButton(
                            onClick = { otpCodeInput = lastOtp ?: "123456" },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Auto-Fill (${lastOtp ?: "123456"})", fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (!isOtpSent) {
                            viewModel.requestOtp(otpEmailInput) { success, msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    isOtpSent = true
                                    otpCodeInput = lastOtp ?: "123456"
                                }
                            }
                        } else {
                            viewModel.verifyOtp(otpEmailInput, otpCodeInput) { success, msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                if (success) showOtpDialog = false
                            }
                        }
                    }
                ) {
                    Text(if (!isOtpSent) "Send Passcode" else "Verify OTP")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOtpDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Owner Login Dialog
    if (showOwnerLoginDialog) {
        AlertDialog(
            onDismissRequest = { showOwnerLoginDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = SanhitaBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Owner Console Login")
                }
            },
            text = {
                Column {
                    Text(
                        text = "Log in with verified App Owner credentials to access the master dashboard:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = ownerEmailInput,
                        onValueChange = { ownerEmailInput = it },
                        label = { Text("Owner Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = ownerPasswordInput,
                        onValueChange = { ownerPasswordInput = it },
                        label = { Text("Owner Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FilledTonalButton(
                        onClick = {
                            ownerEmailInput = "Searkkk@gmail.com"
                            ownerPasswordInput = "Krieshna_14"
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Fill Owner (Searkkk@gmail.com / Krieshna_14)", fontSize = 10.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.loginAsOwner(ownerEmailInput, ownerPasswordInput) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            if (success) {
                                showOwnerLoginDialog = false
                                viewModel.navigateTo(AppScreen.OWNER_CONSOLE)
                            }
                        }
                    }
                ) {
                    Text("Login as Owner")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOwnerLoginDialog = false }) { Text("Cancel") }
            }
        )
    }

    // AI Image Generator Dialog (Uses reading coins)
    if (showImageGenDialog) {
        AlertDialog(
            onDismissRequest = { showImageGenDialog = false },
            title = { Text("AI Concept Illustrator") },
            text = {
                Column {
                    Text(
                        text = "Generate custom editorial artwork for news stories. Cost: 10 reading coins (Reserved & refunded on failure).",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = imagePromptInput,
                        onValueChange = { imagePromptInput = it },
                        placeholder = { Text("e.g. Modern renewable solar grid in South Asia with ascending dawn rays") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.generateImageWithCoins(imagePromptInput) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            if (success) showImageGenDialog = false
                        }
                    },
                    enabled = imagePromptInput.isNotBlank() && coinBalance >= 10
                ) {
                    Text("Generate (10 Coins)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImageGenDialog = false }) { Text("Cancel") }
            }
        )
    }
}
