package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Saved Articles
    @Query("SELECT * FROM saved_articles ORDER BY savedAt DESC")
    fun getAllSavedArticles(): Flow<List<SavedArticleEntity>>

    @Query("SELECT * FROM saved_articles WHERE id = :id LIMIT 1")
    suspend fun getSavedArticle(id: String): SavedArticleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedArticle(article: SavedArticleEntity)

    @Query("DELETE FROM saved_articles WHERE id = :id")
    suspend fun deleteSavedArticle(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_articles WHERE id = :id)")
    suspend fun isArticleSaved(id: String): Boolean

    // Saved Verifications
    @Query("SELECT * FROM saved_verifications ORDER BY checkedAt DESC")
    fun getAllSavedVerifications(): Flow<List<SavedVerificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedVerification(report: SavedVerificationEntity)

    @Query("DELETE FROM saved_verifications WHERE id = :id")
    suspend fun deleteSavedVerification(id: String)

    // Chat Messages
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatMessages()

    // Reward Ledger
    @Query("SELECT * FROM reward_ledger ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<RewardTransactionEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM reward_ledger WHERE idempotencyKey = :key)")
    suspend fun hasTransactionKey(key: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransaction(tx: RewardTransactionEntity): Long

    @Query("SELECT SUM(amount) FROM reward_ledger WHERE status = 'COMPLETED'")
    suspend fun getTotalCoinBalance(): Int?

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)
}
