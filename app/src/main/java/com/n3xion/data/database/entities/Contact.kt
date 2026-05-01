package com.n3xion.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey
    val phoneNumber: String,
    val name: String,
    val lastMessage: String = "",
    val lastMessageTime: Long = 0,
    val unreadCount: Int = 0,
    val isN3xionUser: Boolean = false,
    val publicKey: String? = null
)
