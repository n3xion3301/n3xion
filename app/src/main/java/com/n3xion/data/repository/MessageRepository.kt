package com.n3xion.data.repository

import androidx.lifecycle.LiveData
import com.n3xion.data.database.MessageDao
import com.n3xion.data.database.entities.Message

class MessageRepository(private val messageDao: MessageDao) {
    
    fun getMessagesForContact(phoneNumber: String): LiveData<List<Message>> {
        return messageDao.getMessagesForContact(phoneNumber)
    }
    
    suspend fun insertMessage(message: Message): Long {
        return messageDao.insertMessage(message)
    }
    
    suspend fun updateMessage(message: Message) {
        messageDao.updateMessage(message)
    }
    
    suspend fun markMessagesAsRead(phoneNumber: String) {
        messageDao.markMessagesAsRead(phoneNumber)
    }
    
    suspend fun deleteConversation(phoneNumber: String) {
        messageDao.deleteConversation(phoneNumber)
    }
    
    suspend fun getUnreadCount(phoneNumber: String): Int {
        return messageDao.getUnreadCount(phoneNumber)
    }
    
    suspend fun deleteMessage(messageId: Long) {
        messageDao.deleteMessage(messageId)
    }
}
