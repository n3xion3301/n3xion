package com.n3xion.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.n3xion.data.database.N3xionDatabase
import com.n3xion.data.database.entities.Contact
import com.n3xion.data.repository.ContactRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val contactRepository: ContactRepository
    val allContacts: LiveData<List<Contact>>
    
    init {
        val database = N3xionDatabase.getDatabase(application)
        contactRepository = ContactRepository(database.contactDao())
        allContacts = contactRepository.allContacts
    }
    
    fun searchContacts(query: String): LiveData<List<Contact>> {
        return contactRepository.searchContacts(query)
    }
    
    fun deleteContact(contact: Contact) = viewModelScope.launch {
        contactRepository.deleteContact(contact)
    }
}
