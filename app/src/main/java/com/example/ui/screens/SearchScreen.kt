package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NewsCard
import com.example.ui.viewmodel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val query by viewModel.searchQuery.collectAsState()
    val results by viewModel.searchResults.collectAsState()
    val translatedArticles by viewModel.translatedArticles.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Top Stories", "India", "World", "Technology", "Business", "Science")

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("search_screen")
    ) {
        // Search Input Bar
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            placeholder = { Text("Search headlines, topics, entities...", fontSize = 13.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("search_input_field"),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        // Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            filters.forEach { filter ->
                val isSel = filter == selectedFilter
                FilterChip(
                    selected = isSel,
                    onClick = {
                        selectedFilter = filter
                        viewModel.onSearchQueryChanged(query)
                    },
                    label = { Text(filter, fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Results Summary
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (query.isBlank()) "Recent & Curated Dispatches" else "Results for \"$query\"",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${results.size} matches",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // Results List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            val displayList = if (selectedFilter == "All") results else results.filter { it.category == selectedFilter }
            items(displayList, key = { it.id }) { article ->
                val translated = translatedArticles[article.id]
                NewsCard(
                    article = article,
                    translatedText = translated,
                    onCardClick = { viewModel.openArticle(article) },
                    onTranslateClick = { viewModel.toggleArticleTranslation(article) },
                    onTalkClick = { viewModel.talkAndLearnMore(article) },
                    onSaveClick = { viewModel.toggleBookmark(article) },
                    onShareClick = {},
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
