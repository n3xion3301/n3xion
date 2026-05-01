package com.n3xion.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.n3xion.data.database.entities.Message

@Dao
interface MessageDao {
    
    @Query("SELECT * FROM messages WHERE phoneNumber = :phoneNumber ORDER BY timestamp DESC")
    fun getMessagesForContact(phoneNumber: String): LiveData<List<Message>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message): Long
    
    @Update
    suspend fun updateMessage(message: Message)
    
    @Query("UPDATE messages SET isRead = 1 WHERE phoneNumber = :phoneNumber AND isSent = 0")
    suspend fun markMessagesAsRead(phoneNumber: String)
    
    @Query("DELETE FROM messages WHERE phoneNumber = :phoneNumber")
    suspend fun deleteConversation(phoneNumber: String)
    
    @Query("SELECT COUNT(*) FROM messages WHERE phoneNumber = :phoneNumber AND isSent = 0 AND isRead = 0")
    suspend fun getUnreadCount(phoneNumber: String): Int
    
    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: Long)
}
