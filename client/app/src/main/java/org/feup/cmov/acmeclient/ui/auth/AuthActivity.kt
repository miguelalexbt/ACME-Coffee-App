package org.feup.cmov.acmeclient.ui.auth

import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.MainApplication.Companion.context
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.databinding.ActivityAuthBinding

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityAuthBinding>(this, R.layout.activity_auth)
        supportActionBar?.hide()

        // Ignore NFC
        NfcAdapter.getDefaultAdapter(context)?.setNdefPushMessage(null, this)
    }
}