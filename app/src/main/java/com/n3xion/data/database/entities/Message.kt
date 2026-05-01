package com.n3xion.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phoneNumber: String,
    val content: String,
    val encryptedContent: String,
    val timestamp: Long,
    val isSent: Boolean,
    val isEncrypted: Boolean,
    val isRead: Boolean = false,
    val deliveryStatus: DeliveryStatus = DeliveryStatus.PENDING
)

enum class DeliveryStatus {
    PENDING,
    SENT,
    DELIVERED,
    FAILED
}
