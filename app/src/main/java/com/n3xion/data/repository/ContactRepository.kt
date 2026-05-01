package com.n3xion.data.repository

import androidx.lifecycle.LiveData
import com.n3xion.data.database.ContactDao
import com.n3xion.data.database.entities.Contact

class ContactRepository(private val contactDao: ContactDao) {
    
    val allContacts: LiveData<List<Contact>> = contactDao.getAllContacts()
    
    suspend fun getContact(phoneNumber: String): Contact? {
        return contactDao.getContact(phoneNumber)
    }
    
    suspend fun insertContact(contact: Contact) {
        contactDao.insertContact(contact)
    }
    
    suspend fun updateContact(contact: Contact) {
        contactDao.updateContact(contact)
    }
    
    suspend fun updateUnreadCount(phoneNumber: String, count: Int) {
        contactDao.updateUnreadCount(phoneNumber, count)
    }
    
    suspend fun updateLastMessage(phoneNumber: String, message: String, time: Long) {
        contactDao.updateLastMessage(phoneNumber, message, time)
    }
    
    suspend fun deleteContact(contact: Contact) {
        contactDao.deleteContact(contact)
    }
    
    fun searchContacts(query: String): LiveData<List<Contact>> {
        return contactDao.searchContacts("%$query%")
    }
}
