package com.n3xion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.n3xion.data.database.N3xionDatabase
import com.n3xion.data.repository.ContactRepository
import com.n3xion.data.repository.MessageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            
            val database = N3xionDatabase.getDatabase(context)
            val messageRepository = MessageRepository(database.messageDao())
            val contactRepository = ContactRepository(database.contactDao())
            val encryptionManager = EncryptionManager()
            val smsHandler = SmsHandler(context, encryptionManager, messageRepository, contactRepository)
            
            CoroutineScope(Dispatchers.IO).launch {
                for (sms in messages) {
                    val sender = sms.displayOriginatingAddress
                    val messageBody = sms.messageBody
                    
                    smsHandler.handleReceivedSms(sender, messageBody)
                    
                    if (messageBody.startsWith("N3X:")) {
                        abortBroadcast()
                    }
                }
            }
        }
    }
}
