package com.n3xion.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.n3xion.databinding.ActivityConversationBinding
import com.n3xion.ui.adapters.MessageAdapter
import com.n3xion.viewmodel.ConversationViewModel

class ConversationActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityConversationBinding
    private val viewModel: ConversationViewModel by viewModels()
    private lateinit var adapter: MessageAdapter
    private lateinit var phoneNumber: String
    private lateinit var contactName: String
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        phoneNumber = intent.getStringExtra("PHONE_NUMBER") ?: return finish()
        contactName = intent.getStringExtra("CONTACT_NAME") ?: phoneNumber
        
        setupToolbar()
        setupRecyclerView()
        observeMessages()
        setupSendButton()
        
        viewModel.markMessagesAsRead(phoneNumber)
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = contactName
            subtitle = phoneNumber
        }
    }
    
    private fun setupRecyclerView() {
        adapter = MessageAdapter()
        
        binding.recyclerMessages.apply {
            layoutManager = LinearLayoutManager(this@ConversationActivity).apply {
                stackFromEnd = true
            }
            adapter = this@ConversationActivity.adapter
        }
    }
    
    private fun observeMessages() {
        viewModel.getMessages(phoneNumber).observe(this) { messages ->
            adapter.submitList(messages.reversed()) {
                if (messages.isNotEmpty()) {
                    binding.recyclerMessages.scrollToPosition(adapter.itemCount - 1)
                }
            }
        }
    }
    
    private fun setupSendButton() {
        binding.buttonSend.setOnClickListener {
            val message = binding.editMessage.text.toString().trim()
            
            if (message.isNotEmpty()) {
                viewModel.sendMessage(phoneNumber, message)
                binding.editMessage.text?.clear()
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
