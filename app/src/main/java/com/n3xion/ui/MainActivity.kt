package com.n3xion.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.n3xion.R
import com.n3xion.databinding.ActivityMainBinding
import com.n3xion.ui.adapters.ConversationAdapter
import com.n3xion.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: ConversationAdapter
    
    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (!allGranted) {
            showPermissionDeniedDialog()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "N3xion"
        
        checkPermissions()
        setupRecyclerView()
        observeContacts()
        setupFab()
    }
    
    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS
        )
        
        val notGranted = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (notGranted.isNotEmpty()) {
            requestPermissions.launch(notGranted.toTypedArray())
        }
    }
    
    private fun setupRecyclerView() {
        adapter = ConversationAdapter { contact ->
            val intent = Intent(this, ConversationActivity::class.java).apply {
                putExtra("PHONE_NUMBER", contact.phoneNumber)
                putExtra("CONTACT_NAME", contact.name)
            }
            startActivity(intent)
        }
        
        binding.recyclerConversations.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }
    
    private fun observeContacts() {
        viewModel.allContacts.observe(this) { contacts ->
            adapter.submitList(contacts)
            
            if (contacts.isEmpty()) {
                binding.textEmptyState.visibility = android.view.View.VISIBLE
                binding.recyclerConversations.visibility = android.view.View.GONE
            } else {
                binding.textEmptyState.visibility = android.view.View.GONE
                binding.recyclerConversations.visibility = android.view.View.VISIBLE
            }
        }
    }
    
    private fun setupFab() {
        binding.fabNewMessage.setOnClickListener {
            showNewMessageDialog()
        }
    }
    
    private fun showNewMessageDialog() {
        val input = android.widget.EditText(this).apply {
            hint = "Phone number"
            inputType = android.text.InputType.TYPE_CLASS_PHONE
        }
        
        MaterialAlertDialogBuilder(this)
            .setTitle("New Conversation")
            .setView(input)
            .setPositiveButton("Start") { _, _ ->
                val phoneNumber = input.text.toString()
                if (phoneNumber.isNotEmpty()) {
                    val intent = Intent(this, ConversationActivity::class.java).apply {
                        putExtra("PHONE_NUMBER", phoneNumber)
                        putExtra("CONTACT_NAME", phoneNumber)
                    }
                    startActivity(intent)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showPermissionDeniedDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Permissions Required")
            .setMessage("N3xion needs SMS permissions to function properly.")
            .setPositiveButton("OK") { _, _ -> checkPermissions() }
            .setCancelable(false)
            .show()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { query ->
                    if (query.isEmpty()) {
                        observeContacts()
                    } else {
                        viewModel.searchContacts(query).observe(this@MainActivity) { contacts ->
                            adapter.submitList(contacts)
                        }
                    }
                }
                return true
            }
        })
        
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_about -> {
                showAboutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("About N3xion")
            .setMessage("N3xion v1.0.0\n\nEnd-to-end encrypted SMS messaging\n\nDeveloped by n3x_ion")
            .setPositiveButton("OK", null)
            .show()
    }
}
