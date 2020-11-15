package org.feup.cmov.acmeclient.ui.main

import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.MainApplication
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setupNavigation()

//        supportActionBar?.hide()
//        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Ignore NFC
        NfcAdapter.getDefaultAdapter(MainApplication.context)?.setNdefPushMessage(null, this)
    }

    private fun setupNavigation() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_content)
        NavigationUI.setupWithNavController(navView, navHostFragment!!.findNavController())
    }
}