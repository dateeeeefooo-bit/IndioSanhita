package com.example.data.model

data class NewsArticle(
    val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val category: String, // Top, India, World, Business, Technology, Science, Health, Sports, Entertainment, Explainers
    val sourceName: String,
    val sourceDomain: String,
    val sourceUrl: String,
    val publishedAt: Long,
    val updatedAt: Long,
    val imageUrl: String,
    val imageCredit: String,
    val readingTimeMinutes: Int,
    val isBookmarked: Boolean = false,
    val language: String = "en",
    val aiOverview: String = "",
    val whatIsClear: List<String> = emptyList(),
    val whatRemainsUnclear: List<String> = emptyList(),
    val relatedStories: List<String> = emptyList(),
    val videoUrl: String? = null
)

data class VerificationEvidence(
    val sourceTitle: String,
    val url: String,
    val publishedAt: String,
    val snippet: String,
    val stance: String // "supports", "refutes", "neutral"
)

data class VerificationReport(
    val id: String,
    val claim: String,
    val verdict: String, // "Supported", "Mostly supported", "Mixed/unclear", "Unsupported", "Misleading context", "Unable to verify"
    val confidence: String, // "Low", "Medium", "High"
    val summary: String,
    val evidenceFor: List<VerificationEvidence>,
    val evidenceAgainst: List<VerificationEvidence>,
    val limitations: List<String>,
    val checkedAt: Long,
    val nextSteps: List<String>,
    val isSaved: Boolean = false
)

data class ChatCitation(
    val title: String,
    val source: String,
    val url: String
)

data class ChatMessage(
    val id: String,
    val role: String, // "user" or "assistant"
    val content: String,
    val provider: String = "Gemini", // "Gemini" or "INDOSANHITA Intelligence"
    val style: String = "Balanced",
    val length: String = "Medium",
    val citations: List<ChatCitation> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val attachmentName: String? = null,
    val attachmentType: String? = null, // "image", "url", "pdf"
    val isStreaming: Boolean = false,
    val helpfulFeedback: Boolean? = null
)

enum class UserRole {
    READER, FACT_CHECKER, OWNER
}

data class UserProfile(
    val id: String,
    val email: String,
    val displayName: String,
    val role: UserRole = UserRole.READER,
    val coinBalance: Int = 120,
    val dailyCoinsEarned: Int = 15,
    val maxDailyCoins: Int = 50,
    val readingStreakDays: Int = 4,
    val articlesReadCount: Int = 23,
    val isAuthenticated: Boolean = true
)

data class RewardTransaction(
    val id: String,
    val idempotencyKey: String,
    val userId: String,
    val eventType: String, // "read_article", "verify_claim", "daily_streak", "image_generation"
    val amount: Int, // positive for earn, negative for spend
    val description: String,
    val timestamp: Long,
    val status: String = "COMPLETED" // "COMPLETED", "RESERVED", "REFUNDED"
)

data class OwnerAuditLog(
    val id: String,
    val actor: String,
    val action: String,
    val target: String,
    val reason: String,
    val timestamp: Long,
    val result: String
)

data class ServiceHealth(
    val name: String,
    val status: String, // "HEALTHY", "DEGRADED", "NOT_CONFIGURED"
    val latencyMs: Long,
    val lastCheckTime: Long,
    val details: String
)

data class DocumentInfo(
    val name: String,
    val fileType: String, // "PDF", "TXT", "DOC"
    val sizeText: String,
    val pageCount: Int,
    val title: String,
    val extractedText: String,
    val summary: String,
    val claimsFound: List<String>
)

data class AppNotification(
    val id: String,
    val title: String,
    val body: String,
    val category: String, // "BREAKING", "AI_SUGGESTION", "EMAIL_DIGEST", "VERIFICATION"
    val timestamp: Long,
    val isRead: Boolean = false,
    val actionUrl: String? = null
)

data class RegisteredUserInfo(
    val id: String,
    val email: String,
    val displayName: String,
    val role: UserRole,
    val coinBalance: Int,
    val deviceType: String,
    val lastActive: String,
    val status: String
)

