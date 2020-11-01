package org.feup.cmov.acmeclient.ui.main

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setupNavigation()
    }

    private fun setupNavigation() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_content)
        NavigationUI.setupWithNavController(navView, navHostFragment!!.findNavController())
    }

    override fun onResume() {
        super.onResume()
        receiveMessageFromDevice(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun receiveMessageFromDevice(intent: Intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            val msg = rawMsgs!![0] as NdefMessage
            val messageString = String(msg.records[0].payload)
            println(messageString)
        }
        // gets an order message and passes it to the MainActivity
//        val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
//        val msg = rawMsgs!![0] as NdefMessage
//        val message = msg.records[0].payload
//        val main = Intent(this, MainActivity::class.java)
//        main.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//        main.putExtra("order", message)
//        main.putExtra("type", 2)
//        startActivity(main)
    }
}