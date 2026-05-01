package com.n3xion.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.n3xion.data.database.entities.Message
import com.n3xion.databinding.ItemMessageReceivedBinding
import com.n3xion.databinding.ItemMessageSentBinding
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter : ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback()) {
    
    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }
    
    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isSent) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENT -> {
                val binding = ItemMessageSentBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                SentMessageViewHolder(binding)
            }
            else -> {
                val binding = ItemMessageReceivedBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ReceivedMessageViewHolder(binding)
            }
        }
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is SentMessageViewHolder -> holder.bind(message)
            is ReceivedMessageViewHolder -> holder.bind(message)
        }
    }
    
    class SentMessageViewHolder(
        private val binding: ItemMessageSentBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(message: Message) {
            binding.apply {
                textMessage.text = message.content
                textTimestamp.text = formatTime(message.timestamp)
                
                iconEncrypted.visibility = if (message.isEncrypted) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }
                
                textStatus.text = when (message.deliveryStatus) {
                    com.n3xion.data.database.entities.DeliveryStatus.PENDING -> "Sending..."
                    com.n3xion.data.database.entities.DeliveryStatus.SENT -> "Sent"
                    com.n3xion.data.database.entities.DeliveryStatus.DELIVERED -> "Delivered"
                    com.n3xion.data.database.entities.DeliveryStatus.FAILED -> "Failed"
                }
            }
        }
    }
    
    class ReceivedMessageViewHolder(
        private val binding: ItemMessageReceivedBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(message: Message) {
            binding.apply {
                textMessage.text = message.content
                textTimestamp.text = formatTime(message.timestamp)
                
                iconEncrypted.visibility = if (message.isEncrypted) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }
            }
        }
    }
    
    class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
    
    companion object {
        private fun formatTime(timestamp: Long): String {
            return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
        }
    }
}
