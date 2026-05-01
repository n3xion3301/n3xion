package com.n3xion.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.n3xion.EncryptionManager
import com.n3xion.SmsHandler
import com.n3xion.data.database.N3xionDatabase
import com.n3xion.data.database.entities.Message
import com.n3xion.data.repository.ContactRepository
import com.n3xion.data.repository.MessageRepository
import kotlinx.coroutines.launch

class ConversationViewModel(application: Application) : AndroidViewModel(application) {
    
    private val messageRepository: MessageRepository
    private val contactRepository: ContactRepository
    private val smsHandler: SmsHandler
    
    init {
        val database = N3xionDatabase.getDatabase(application)
        messageRepository = MessageRepository(database.messageDao())
        contactRepository = ContactRepository(database.contactDao())
        val encryptionManager = EncryptionManager()
        smsHandler = SmsHandler(application, encryptionManager, messageRepository, contactRepository)
    }
    
    fun getMessages(phoneNumber: String): LiveData<List<Message>> {
        return messageRepository.getMessagesForContact(phoneNumber)
    }
    
    fun sendMessage(phoneNumber: String, message: String) {
        smsHandler.sendEncryptedSms(phoneNumber, message)
    }
    
    fun markMessagesAsRead(phoneNumber: String) = viewModelScope.launch {
        messageRepository.markMessagesAsRead(phoneNumber)
        contactRepository.updateUnreadCount(phoneNumber, 0)
    }
    
    fun deleteMessage(messageId: Long) = viewModelScope.launch {
        messageRepository.deleteMessage(messageId)
    }
    
    fun deleteConversation(phoneNumber: String) = viewModelScope.launch {
        messageRepository.deleteConversation(phoneNumber)
    }
}
