package com.n3xion.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.n3xion.data.database.entities.Contact
import com.n3xion.databinding.ItemConversationBinding
import java.text.SimpleDateFormat
import java.util.*

class ConversationAdapter(
    private val onContactClick: (Contact) -> Unit
) : ListAdapter<Contact, ConversationAdapter.ConversationViewHolder>(ContactDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val binding = ItemConversationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ConversationViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ConversationViewHolder(
        private val binding: ItemConversationBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(contact: Contact) {
            binding.apply {
                textContactName.text = contact.name
                textLastMessage.text = contact.lastMessage
                textTimestamp.text = formatTimestamp(contact.lastMessageTime)
                
                if (contact.unreadCount > 0) {
                    badgeUnread.visibility = android.view.View.VISIBLE
                    badgeUnread.text = contact.unreadCount.toString()
                } else {
                    badgeUnread.visibility = android.view.View.GONE
                }
                
                iconEncrypted.visibility = if (contact.isN3xionUser) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }
                
                root.setOnClickListener { onContactClick(contact) }
            }
        }
        
        private fun formatTimestamp(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp
            
            return when {
                diff < 60000 -> "Just now"
                diff < 3600000 -> "${diff / 60000}m ago"
                diff < 86400000 -> "${diff / 3600000}h ago"
                diff < 604800000 -> SimpleDateFormat("EEE", Locale.getDefault()).format(Date(timestamp))
                else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
            }
        }
    }
    
    class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.phoneNumber == newItem.phoneNumber
        }
        
        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }
    }
}
