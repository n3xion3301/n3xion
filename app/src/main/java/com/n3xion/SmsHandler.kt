package com.n3xion

import android.content.Context
import android.telephony.SmsManager
import android.widget.Toast
import com.n3xion.data.database.entities.Contact
import com.n3xion.data.database.entities.DeliveryStatus
import com.n3xion.data.database.entities.Message
import com.n3xion.data.repository.ContactRepository
import com.n3xion.data.repository.MessageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsHandler(
    private val context: Context,
    private val encryptionManager: EncryptionManager,
    private val messageRepository: MessageRepository,
    private val contactRepository: ContactRepository
) {
    
    fun sendEncryptedSms(phoneNumber: String, messageText: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val encrypted = encryptionManager.encrypt(messageText)
                val smsText = "N3X:${encrypted.toSmsFormat()}"
                
                val message = Message(
                    phoneNumber = phoneNumber,
                    content = messageText,
                    encryptedContent = smsText,
                    timestamp = System.currentTimeMillis(),
                    isSent = true,
                    isEncrypted = true,
                    deliveryStatus = DeliveryStatus.PENDING
                )
                
                val messageId = messageRepository.insertMessage(message)
                
                contactRepository.updateLastMessage(
                    phoneNumber,
                    messageText,
                    message.timestamp
                )
                
                val smsManager = SmsManager.getDefault()
                val parts = smsManager.divideMessage(smsText)
                
                if (parts.size > 1) {
                    smsManager.sendMultipartTextMessage(
                        phoneNumber, null, parts, null, null
                    )
                } else {
                    smsManager.sendTextMessage(
                        phoneNumber, null, smsText, null, null
                    )
                }
                
                messageRepository.updateMessage(
                    message.copy(id = messageId, deliveryStatus = DeliveryStatus.SENT)
                )
                
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "Encrypted message sent", Toast.LENGTH_SHORT).show()
                }
                
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    suspend fun handleReceivedSms(phoneNumber: String, smsText: String) {
        try {
            val decrypted = if (smsText.startsWith("N3X:")) {
                val encryptedPart = smsText.removePrefix("N3X:")
                val encryptedData = EncryptedData.fromSmsFormat(encryptedPart)
                encryptedData?.let { encryptionManager.decrypt(it) }
            } else {
                smsText
            }
            
            if (decrypted != null) {
                val message = Message(
                    phoneNumber = phoneNumber,
                    content = decrypted,
                    encryptedContent = smsText,
                    timestamp = System.currentTimeMillis(),
                    isSent = false,
                    isEncrypted = smsText.startsWith("N3X:"),
                    isRead = false
                )
                
                messageRepository.insertMessage(message)
                
                var contact = contactRepository.getContact(phoneNumber)
                if (contact == null) {
                    contact = Contact(
                        phoneNumber = phoneNumber,
                        name = phoneNumber,
                        isN3xionUser = smsText.startsWith("N3X:")
                    )
                    contactRepository.insertContact(contact)
                }
                
                val unreadCount = messageRepository.getUnreadCount(phoneNumber)
                contactRepository.updateLastMessage(phoneNumber, decrypted, message.timestamp)
                contactRepository.updateUnreadCount(phoneNumber, unreadCount)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
