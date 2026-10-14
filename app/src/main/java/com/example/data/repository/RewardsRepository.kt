package com.example.data.repository

import com.example.data.db.AppDao
import com.example.data.db.RewardTransactionEntity
import com.example.data.model.RewardTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class RewardsRepository(private val appDao: AppDao) {

    private val DAILY_CAP = 50

    fun getTransactions(): Flow<List<RewardTransaction>> {
        return appDao.getAllTransactions().map { list ->
            list.map { entity ->
                RewardTransaction(
                    id = entity.id,
                    idempotencyKey = entity.idempotencyKey,
                    userId = entity.userId,
                    eventType = entity.eventType,
                    amount = entity.amount,
                    description = entity.description,
                    timestamp = entity.timestamp,
                    status = entity.status
                )
            }
        }
    }

    suspend fun getCoinBalance(): Int {
        val total = appDao.getTotalCoinBalance() ?: 0
        return (100 + total).coerceAtLeast(0) // 100 starting bonus coins
    }

    suspend fun awardCoins(
        userId: String,
        eventType: String,
        amount: Int,
        description: String,
        idempotencyKey: String
    ): Boolean {
        // Enforce anti-replay idempotency
        if (appDao.hasTransactionKey(idempotencyKey)) {
            return false // Already credited
        }

        val entity = RewardTransactionEntity(
            id = UUID.randomUUID().toString(),
            idempotencyKey = idempotencyKey,
            userId = userId,
            eventType = eventType,
            amount = amount,
            description = description,
            timestamp = System.currentTimeMillis(),
            status = "COMPLETED"
        )
        appDao.insertTransaction(entity)
        return true
    }

    suspend fun reserveAndDeductCoinsForImageGen(
        userId: String,
        cost: Int,
        idempotencyKey: String
    ): Boolean {
        val current = getCoinBalance()
        if (current < cost) return false

        val entity = RewardTransactionEntity(
            id = UUID.randomUUID().toString(),
            idempotencyKey = idempotencyKey,
            userId = userId,
            eventType = "image_generation",
            amount = -cost,
            description = "AI Concept Illustration Generation",
            timestamp = System.currentTimeMillis(),
            status = "COMPLETED"
        )
        appDao.insertTransaction(entity)
        return true
    }

    suspend fun refundCoins(
        userId: String,
        cost: Int,
        originalKey: String,
        reason: String
    ) {
        val refundKey = "refund_$originalKey"
        val entity = RewardTransactionEntity(
            id = UUID.randomUUID().toString(),
            idempotencyKey = refundKey,
            userId = userId,
            eventType = "refund",
            amount = cost,
            description = "Refund: $reason",
            timestamp = System.currentTimeMillis(),
            status = "REFUNDED"
        )
        appDao.insertTransaction(entity)
    }
}
