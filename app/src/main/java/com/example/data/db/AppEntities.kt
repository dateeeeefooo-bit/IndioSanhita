package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_articles")
data class SavedArticleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val category: String,
    val sourceName: String,
    val sourceDomain: String,
    val sourceUrl: String,
    val publishedAt: Long,
    val imageUrl: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_verifications")
data class SavedVerificationEntity(
    @PrimaryKey val id: String,
    val claim: String,
    val verdict: String,
    val confidence: String,
    val summary: String,
    val evidenceForJson: String,
    val evidenceAgainstJson: String,
    val limitationsJson: String,
    val checkedAt: Long
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val role: String,
    val content: String,
    val provider: String,
    val style: String,
    val length: String,
    val citationsJson: String,
    val timestamp: Long,
    val attachmentName: String?,
    val attachmentType: String?
)

@Entity(tableName = "reward_ledger")
data class RewardTransactionEntity(
    @PrimaryKey val id: String,
    val idempotencyKey: String,
    val userId: String,
    val eventType: String,
    val amount: Int,
    val description: String,
    val timestamp: Long,
    val status: String
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val actor: String,
    val action: String,
    val target: String,
    val reason: String,
    val timestamp: Long,
    val result: String
)
