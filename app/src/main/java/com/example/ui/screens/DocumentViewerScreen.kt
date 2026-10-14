package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DocumentInfo
import com.example.ui.theme.IndoNavy
import com.example.ui.theme.SanhitaBlue
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentViewerScreen(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentDoc by viewModel.selectedDocument.collectAsState()
    var currentPage by remember { mutableStateOf(1) }
    var userLoadedDocName by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val fileName = uri.lastPathSegment ?: "Imported_Document.pdf"
            userLoadedDocName = fileName
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("document_viewer_screen")
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Document & PDF Opener",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Native document inspection, AI synthesis & claim verification",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            FilledTonalButton(
                onClick = { filePickerLauncher.launch(arrayOf("application/pdf", "text/plain", "*/*")) },
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("open_file_btn")
            ) {
                Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Open File", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Document Selector Chips (Switch between built-in whitepapers)
        Text("Sample Document Archives:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            viewModel.sampleDocuments.forEach { doc ->
                val isSel = doc.name == currentDoc.name
                FilterChip(
                    selected = isSel,
                    onClick = {
                        viewModel.openDocumentViewer(doc)
                        currentPage = 1
                    },
                    label = { Text(doc.name, fontSize = 11.sp, maxLines = 1) },
                    modifier = Modifier.testTag("doc_chip_${doc.name}")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Document Metadata Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
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
                            text = "${currentDoc.fileType} • ${currentDoc.sizeText} • ${currentDoc.pageCount} Pages",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Page Navigation
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (currentPage > 1) currentPage-- },
                            enabled = currentPage > 1,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Prev page")
                        }
                        Text(
                            text = "Page $currentPage / ${currentDoc.pageCount}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { if (currentPage < currentDoc.pageCount) currentPage++ },
                            enabled = currentPage < currentDoc.pageCount,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Next page")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = userLoadedDocName ?: currentDoc.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = currentDoc.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick AI Document Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    viewModel.setChatAttachment("Document: ${currentDoc.name}")
                    viewModel.navigateTo(AppScreen.AI_CHAT)
                    viewModel.sendChatMessage("Analyze and summarize key obligations and findings in this document: ${currentDoc.title}")
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("doc_summarize_btn")
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Summarize with AI", fontSize = 11.sp)
            }

            OutlinedButton(
                onClick = {
                    val firstClaim = currentDoc.claimsFound.firstOrNull() ?: currentDoc.summary
                    viewModel.navigateTo(AppScreen.VERIFY)
                    viewModel.runVerification(firstClaim, "pdf")
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("doc_factcheck_btn")
            ) {
                Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Fact-Check Claims", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Document Reader Preview Box (Simulated Page Sheet with Real Extracted Content)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "DOCUMENT TEXT STREAM (PAGE $currentPage)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, currentDoc.title)
                                putExtra(Intent.EXTRA_TEXT, currentDoc.extractedText)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Document Text"))
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentDoc.extractedText,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Extracted Verifiable Claims in Document
        Text("Identified Claims for Fact-Checking:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        currentDoc.claimsFound.forEach { claim ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = "• $claim",
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            viewModel.navigateTo(AppScreen.VERIFY)
                            viewModel.runVerification(claim, "pdf")
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Verify claim", tint = SanhitaBlue)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
