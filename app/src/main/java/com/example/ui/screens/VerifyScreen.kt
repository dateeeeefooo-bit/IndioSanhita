package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.example.ui.theme.IndoNavy
import com.example.ui.theme.SanhitaBlue
import com.example.ui.viewmodel.NewsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyScreen(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isVerifying by viewModel.isVerifying.collectAsState()
    val report by viewModel.currentVerificationReport.collectAsState()

    var claimInput by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) } // 0: Text Claim, 1: URL, 2: Screenshot/Doc
    var attachedAsset by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            attachedAsset = uri.lastPathSegment ?: "Document/Screenshot"
            if (claimInput.isBlank()) {
                claimInput = "85 nations ratified the New Delhi clean energy grid synchronization framework"
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("verify_screen")
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.FactCheck,
                contentDescription = null,
                tint = SanhitaBlue,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "News Claim Verification",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Source-aware evidence corroboration with transparent uncertainty reporting",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input Type Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Text Claim", fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Article URL", fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Screenshot / PDF", fontSize = 12.sp) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Input Box
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (selectedTab == 2) {
                    OutlinedButton(
                        onClick = { filePickerLauncher.launch("*/*") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("upload_screenshot_btn")
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (attachedAsset != null) "Uploaded: $attachedAsset" else "Upload Screenshot or PDF")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = claimInput,
                    onValueChange = { claimInput = it },
                    label = {
                        Text(
                            when (selectedTab) {
                                1 -> "Paste Article or Post URL"
                                2 -> "Extracted Claim (Editable for review)"
                                else -> "Enter statement or quote to verify"
                            }
                        )
                    },
                    placeholder = {
                        Text("e.g. 85 countries signed the New Delhi Clean Energy Accord allocating $120B in credit backing")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("claim_input_field"),
                    minLines = 3,
                    maxLines = 6,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Verify Button
                Button(
                    onClick = {
                        val type = when (selectedTab) {
                            1 -> "url"
                            2 -> "image"
                            else -> "text"
                        }
                        viewModel.runVerification(claimInput, type)
                    },
                    enabled = claimInput.isNotBlank() && !isVerifying,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_verify_btn")
                ) {
                    if (isVerifying) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cross-referencing global sources...")
                    } else {
                        Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Verify with INDOSANHITA Engine")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Verification Report Card
        AnimatedVisibility(visible = report != null) {
            report?.let { rep ->
                val verdictColor = when (rep.verdict) {
                    "Supported" -> Color(0xFF147D64)
                    "Mostly supported" -> Color(0xFF0D9488)
                    "Mixed/unclear" -> Color(0xFFB7791F)
                    "Unsupported" -> Color(0xFFB42318)
                    "Misleading context" -> Color(0xFFE11D48)
                    else -> Color(0xFF64748B)
                }

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("verification_report_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Verdict Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = verdictColor.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = when (rep.verdict) {
                                            "Supported", "Mostly supported" -> Icons.Default.CheckCircle
                                            "Unsupported", "Misleading context" -> Icons.Default.Cancel
                                            else -> Icons.Default.Help
                                        },
                                        contentDescription = null,
                                        tint = verdictColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = rep.verdict.uppercase(),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = verdictColor
                                    )
                                }
                            }

                            Text(
                                text = "Confidence: ${rep.confidence}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Claim Tested:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "\"${rep.claim}\"",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = rep.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Supporting Evidence
                        if (rep.evidenceFor.isNotEmpty()) {
                            Text(
                                text = "CORROBORATING EVIDENCE (${rep.evidenceFor.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF147D64)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            rep.evidenceFor.forEach { ev ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(text = ev.sourceTitle, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(text = ev.snippet, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(text = "Published: ${ev.publishedAt}", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // Contradicting Evidence
                        if (rep.evidenceAgainst.isNotEmpty()) {
                            Text(
                                text = "CONTRADICTING / REFUTING EVIDENCE (${rep.evidenceAgainst.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB42318)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            rep.evidenceAgainst.forEach { ev ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(text = ev.sourceTitle, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(text = ev.snippet, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(text = "Published: ${ev.publishedAt}", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // Limitations & Unknowns
                        if (rep.limitations.isNotEmpty()) {
                            Text(
                                text = "SYSTEM LIMITATIONS & UNKNOWNS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            rep.limitations.forEach { lim ->
                                Text(
                                    text = "• $lim",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(rep.checkedAt))
                        Text(
                            text = "Checked timestamp: $dateStr • AI-assisted verification report",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons: Save & Share
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.saveVerificationReport(rep) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("save_report_btn")
                            ) {
                                Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (rep.isSaved) "Saved" else "Save Report", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, "INDOSANHITA Verification: ${rep.verdict}")
                                        putExtra(Intent.EXTRA_TEXT, "Claim: ${rep.claim}\nVerdict: ${rep.verdict}\nSummary: ${rep.summary}\nVerified via INDOSANHITA Global News")
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Verification Report"))
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("share_report_btn")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share Badge", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
