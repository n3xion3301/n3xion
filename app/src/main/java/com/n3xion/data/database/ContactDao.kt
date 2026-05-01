package com.n3xion.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.n3xion.data.database.entities.Contact

@Dao
interface ContactDao {
    
    @Query("SELECT * FROM contacts ORDER BY lastMessageTime DESC")
    fun getAllContacts(): LiveData<List<Contact>>
    
    @Query("SELECT * FROM contacts WHERE phoneNumber = :phoneNumber")
    suspend fun getContact(phoneNumber: String): Contact?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact)
    
    @Update
    suspend fun updateContact(contact: Contact)
    
    @Query("UPDATE contacts SET unreadCount = :count WHERE phoneNumber = :phoneNumber")
    suspend fun updateUnreadCount(phoneNumber: String, count: Int)
    
    @Query("UPDATE contacts SET lastMessage = :message, lastMessageTime = :time WHERE phoneNumber = :phoneNumber")
    suspend fun updateLastMessage(phoneNumber: String, message: String, time: Long)
    
    @Delete
    suspend fun deleteContact(contact: Contact)
    
    @Query("SELECT * FROM contacts WHERE phoneNumber LIKE :query OR name LIKE :query")
    fun searchContacts(query: String): LiveData<List<Contact>>
}
