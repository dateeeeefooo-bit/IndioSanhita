package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.DocumentInfo
import com.example.data.model.NewsArticle
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.example.data.model.VerificationReport
import com.example.data.repository.AiChatRepository
import com.example.data.repository.AuthAndOwnerRepository
import com.example.data.repository.NewsRepository
import com.example.data.repository.RewardsRepository
import com.example.data.repository.TranslationRepository
import com.example.data.repository.VerificationRepository
import com.example.service.NewsAudioPlaybackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppScreen {
    HOME,
    SEARCH,
    ARTICLE_DETAIL,
    AI_CHAT,
    VERIFY,
    DOCUMENT_VIEWER,
    VIDEO_PLAYER,
    SAVED,
    PROFILE,
    OWNER_CONSOLE
}

class NewsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val newsRepo = NewsRepository(db.appDao())
    val aiRepo = AiChatRepository(db.appDao())
    val verifyRepo = VerificationRepository(db.appDao())
    val transRepo = TranslationRepository()
    val rewardsRepo = RewardsRepository(db.appDao())
    val authRepo = AuthAndOwnerRepository(db.appDao())

    // Navigation Stack
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen = _currentScreen.asStateFlow()

    private val screenHistory = mutableListOf<AppScreen>()

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            screenHistory.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenHistory.isNotEmpty()) {
            _currentScreen.value = screenHistory.removeAt(screenHistory.size - 1)
            return true
        }
        if (_currentScreen.value != AppScreen.HOME) {
            _currentScreen.value = AppScreen.HOME
            return true
        }
        return false
    }

    // Home / Feed State
    private val _selectedCategory = MutableStateFlow("Top Stories")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _articles = MutableStateFlow<List<NewsArticle>>(emptyList())
    val articles = _articles.asStateFlow()

    private val _selectedArticle = MutableStateFlow<NewsArticle?>(null)
    val selectedArticle = _selectedArticle.asStateFlow()

    // Global Language & Translations
    private val _appLanguage = MutableStateFlow("en")
    val appLanguage = _appLanguage.asStateFlow()

    // Map of articleId -> translated headline/summary
    private val _translatedArticles = MutableStateFlow<Map<String, Pair<String, String>>>(emptyMap())
    val translatedArticles = _translatedArticles.asStateFlow()

    // Search State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<NewsArticle>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    // AI Chat State
    private val _selectedAiProvider = MutableStateFlow("INDOSANHITA Intelligence")
    val selectedAiProvider = _selectedAiProvider.asStateFlow()

    private val _selectedAiStyle = MutableStateFlow("Balanced")
    val selectedAiStyle = _selectedAiStyle.asStateFlow()

    private val _selectedAiLength = MutableStateFlow("Medium")
    val selectedAiLength = _selectedAiLength.asStateFlow()

    private val _isAiStreaming = MutableStateFlow(false)
    val isAiStreaming = _isAiStreaming.asStateFlow()

    private val _aiStreamingText = MutableStateFlow("")
    val aiStreamingText = _aiStreamingText.asStateFlow()

    private val _chatAttachmentName = MutableStateFlow<String?>(null)
    val chatAttachmentName = _chatAttachmentName.asStateFlow()

    val chatMessages = aiRepo.getChatMessages()

    // Verification State
    private val _currentVerificationReport = MutableStateFlow<VerificationReport?>(null)
    val currentVerificationReport = _currentVerificationReport.asStateFlow()

    private val _isVerifying = MutableStateFlow(false)
    val isVerifying = _isVerifying.asStateFlow()

    val savedVerifications = verifyRepo.getSavedReports()
    val savedArticles = newsRepo.getAllSavedArticles()

    // Document / PDF Viewer State
    val sampleDocuments = listOf(
        DocumentInfo(
            name = "Clean_Energy_Summit_Accord_2026.pdf",
            fileType = "PDF",
            sizeText = "1.4 MB",
            pageCount = 18,
            title = "International Renewable Grid Harmonization Accord 2026",
            extractedText = "ARTICLE I: OBJECTIVES & SCOPE\n1.1 The Signatory Parties agree to establish unified high-voltage transmission frequency tolerances not exceeding +/- 0.05 Hz across cross-border corridors.\n\nARTICLE II: MULTILATERAL CREDIT GUARANTEES\n2.1 A pooled sovereign escrow facility of One Hundred and Twenty Billion United States Dollars ($120,000,000,000) is hereby constituted under the joint oversight of participating national treasury authorities.\n\nARTICLE III: DISPUTE SETTLEMENT\n3.1 Mandatory conciliation shall precede any arbitral claim under the UNCITRAL rules.",
            summary = "Official multilateral accord draft establishing 2035 cross-border clean energy grid standards and a $120B sovereign escrow credit facility.",
            claimsFound = listOf(
                "85 nations signed the New Delhi Clean Energy Accord.",
                "$120 billion allocated for developing nation green micro-grids.",
                "Grid synchronization rules mandate frequency tolerance under +/- 0.05 Hz."
            )
        ),
        DocumentInfo(
            name = "NISAR_Mission_Calibration_Report.pdf",
            fileType = "PDF",
            sizeText = "3.2 MB",
            pageCount = 34,
            title = "NASA-ISRO SAR Telemetry & Phase-Two Calibration",
            extractedText = "SECTION 2: DUAL-FREQUENCY PIPELINE VALIDATION\nRadar interferometry routines conducted across the Bengaluru downlink terminal verified phase coherence at 99.4% across 45 sequential orbital sweeps. Soil moisture calibration errors were constrained within 0.04 m3/m3.",
            summary = "Technical evaluation confirming 99.4% phase coherence for ISRO-NASA radar telemetry scanning.",
            claimsFound = listOf(
                "Radar provides centimeter-scale ground movement detection globally every 12 days.",
                "Both L-band and S-band processors achieve synchronized downlink."
            )
        )
    )

    private val _selectedDocument = MutableStateFlow(sampleDocuments[0])
    val selectedDocument = _selectedDocument.asStateFlow()

    // Video Player State
    private val _activeVideoArticle = MutableStateFlow<NewsArticle?>(null)
    val activeVideoArticle = _activeVideoArticle.asStateFlow()

    // User & Rewards
    val currentUser = authRepo.currentUser
    val rewardTransactions = rewardsRepo.getTransactions()
    private val _coinBalance = MutableStateFlow(120)
    val coinBalance = _coinBalance.asStateFlow()

    // Theme (Light, Dark, System)
    private val _themeMode = MutableStateFlow("system") // "light", "dark", "system"
    val themeMode = _themeMode.asStateFlow()

    // Article Typography Scale
    private val _articleTextScale = MutableStateFlow(1.0f) // 0.85f to 1.35f
    val articleTextScale = _articleTextScale.asStateFlow()

    init {
        loadArticles()
        refreshCoinBalance()
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
    }

    fun setArticleTextScale(scale: Float) {
        _articleTextScale.value = scale.coerceIn(0.85f, 1.4f)
    }

    fun selectCategory(cat: String) {
        _selectedCategory.value = cat
        loadArticles()
    }

    fun loadArticles() {
        _articles.value = newsRepo.getArticles(_selectedCategory.value)
    }

    fun openArticle(article: NewsArticle) {
        _selectedArticle.value = article
        navigateTo(AppScreen.ARTICLE_DETAIL)

        // Award reading reward coins idempotently
        viewModelScope.launch {
            val key = "read_${article.id}_${currentUser.value.id}"
            val rewarded = rewardsRepo.awardCoins(
                userId = currentUser.value.id,
                eventType = "read_article",
                amount = 5,
                description = "Read article: ${article.title.take(30)}...",
                idempotencyKey = key
            )
            if (rewarded) refreshCoinBalance()
        }
    }

    fun openVideoPlayer(article: NewsArticle) {
        _activeVideoArticle.value = article
        navigateTo(AppScreen.VIDEO_PLAYER)
    }

    fun openDocumentViewer(doc: DocumentInfo? = null) {
        if (doc != null) _selectedDocument.value = doc
        navigateTo(AppScreen.DOCUMENT_VIEWER)
    }

    fun talkAndLearnMore(article: NewsArticle) {
        _selectedArticle.value = article
        _chatAttachmentName.value = "Article Context: ${article.title}"
        navigateTo(AppScreen.AI_CHAT)
    }

    fun toggleBookmark(article: NewsArticle) {
        viewModelScope.launch {
            if (newsRepo.isArticleSaved(article.id)) {
                newsRepo.removeSavedArticle(article.id)
            } else {
                newsRepo.saveArticle(article)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _searchResults.value = newsRepo.searchArticles(query)
    }

    fun setAppLanguage(langCode: String) {
        _appLanguage.value = langCode
    }

    fun toggleArticleTranslation(article: NewsArticle) {
        viewModelScope.launch {
            val current = _translatedArticles.value[article.id]
            if (current != null) {
                // Toggle off
                _translatedArticles.value = _translatedArticles.value - article.id
            } else {
                // Translate
                val target = _appLanguage.value.ifBlank { "hi" }
                val titleTrans = transRepo.translateText(article.title, target)
                val summaryTrans = transRepo.translateText(article.summary, target)
                _translatedArticles.value = _translatedArticles.value + (article.id to Pair(titleTrans.translatedText, summaryTrans.translatedText))
            }
        }
    }

    // AI Chat controls
    fun setAiProvider(provider: String) { _selectedAiProvider.value = provider }
    fun setAiStyle(style: String) { _selectedAiStyle.value = style }
    fun setAiLength(length: String) { _selectedAiLength.value = length }
    fun setChatAttachment(name: String?) { _chatAttachmentName.value = name }

    fun sendChatMessage(prompt: String) {
        if (prompt.isBlank()) return
        val currentAttachment = _chatAttachmentName.value
        _chatAttachmentName.value = null

        viewModelScope.launch {
            // Save user prompt
            val userMsg = com.example.data.model.ChatMessage(
                id = UUID.randomUUID().toString(),
                role = "user",
                content = prompt,
                provider = _selectedAiProvider.value,
                style = _selectedAiStyle.value,
                length = _selectedAiLength.value,
                attachmentName = currentAttachment,
                timestamp = System.currentTimeMillis()
            )
            aiRepo.saveMessage(userMsg)

            _isAiStreaming.value = true
            _aiStreamingText.value = ""

            aiRepo.streamChatResponse(
                userPrompt = prompt,
                provider = _selectedAiProvider.value,
                style = _selectedAiStyle.value,
                length = _selectedAiLength.value,
                articleContext = _selectedArticle.value,
                attachmentName = currentAttachment
            ).collect { partial ->
                _aiStreamingText.value = partial
            }

            _isAiStreaming.value = false
            _aiStreamingText.value = ""
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            aiRepo.clearHistory()
        }
    }

    // Verification
    fun runVerification(claim: String, inputType: String = "text") {
        if (claim.isBlank()) return
        viewModelScope.launch {
            _isVerifying.value = true
            val report = verifyRepo.verifyClaim(claim, inputType)
            _currentVerificationReport.value = report
            _isVerifying.value = false

            // Award verification reward
            val key = "verify_${report.id}_${currentUser.value.id}"
            val rewarded = rewardsRepo.awardCoins(
                userId = currentUser.value.id,
                eventType = "verify_claim",
                amount = 10,
                description = "Claim verification completed",
                idempotencyKey = key
            )
            if (rewarded) refreshCoinBalance()
        }
    }

    fun saveVerificationReport(report: VerificationReport) {
        viewModelScope.launch {
            verifyRepo.saveReport(report)
            _currentVerificationReport.value = report.copy(isSaved = true)
        }
    }

    fun generateImageWithCoins(prompt: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val key = "imggen_${System.currentTimeMillis()}_${currentUser.value.id}"
            val deducted = rewardsRepo.reserveAndDeductCoinsForImageGen(currentUser.value.id, 10, key)
            if (!deducted) {
                onResult(false, "Insufficient reading coins (10 coins required).")
                return@launch
            }
            refreshCoinBalance()
            onResult(true, "Concept illustration generation committed successfully.")
        }
    }

    fun refreshCoinBalance() {
        viewModelScope.launch {
            _coinBalance.value = rewardsRepo.getCoinBalance()
        }
    }

    // Audio Playback
    fun startAudioOverview(article: NewsArticle, speed: Float = 1.0f) {
        val lang = _appLanguage.value
        val textToSpeak = if (article.aiOverview.isNotBlank()) {
            article.aiOverview
        } else {
            "${article.title}. ${article.summary}"
        }
        NewsAudioPlaybackService.startAudio(
            context = getApplication(),
            title = article.title,
            source = article.sourceName,
            textToSpeak = textToSpeak,
            lang = lang,
            speed = speed
        )
    }

    fun toggleAudioPlayback() {
        NewsAudioPlaybackService.togglePlayPause(getApplication())
    }

    fun setAudioSpeed(speed: Float) {
        NewsAudioPlaybackService.setSpeed(getApplication(), speed)
    }

    fun stopAudio() {
        NewsAudioPlaybackService.stop(getApplication())
    }

    val notifications = authRepo.notifications

    fun dismissNotification(id: String) {
        authRepo.dismissNotification(id)
    }

    fun broadcastNotification(title: String, body: String, category: String) {
        viewModelScope.launch {
            authRepo.broadcastNotification(title, body, category)
        }
    }

    // Auth & Owner actions
    fun loginAsOwner(email: String, passwordAttempt: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = authRepo.loginAsOwner(email, passwordAttempt)
            if (res.isSuccess) {
                onResult(true, "Welcome Owner Searkkk! Access granted to Owner Console.")
            } else {
                onResult(false, res.exceptionOrNull()?.message ?: "Owner authentication failed.")
            }
        }
    }

    fun authenticateOwner(secretCode: String, onResult: (Boolean, String) -> Unit) {
        loginAsOwner("Searkkk@gmail.com", secretCode, onResult)
    }

    fun requestOtp(email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = authRepo.requestEmailOtp(email)
            if (res.isSuccess) {
                onResult(true, res.getOrNull() ?: "OTP Sent")
            } else {
                onResult(false, res.exceptionOrNull()?.message ?: "Error sending OTP")
            }
        }
    }

    fun verifyOtp(email: String, code: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = authRepo.verifyEmailOtp(email, code)
            if (res.isSuccess) {
                onResult(true, "Authenticated as ${res.getOrNull()?.displayName}")
            } else {
                onResult(false, res.exceptionOrNull()?.message ?: "Verification failed")
            }
        }
    }

    fun signOut() {
        authRepo.signOut()
    }
}
