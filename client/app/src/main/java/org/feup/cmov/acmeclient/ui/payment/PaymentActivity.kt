package org.feup.cmov.acmeclient.ui.payment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.databinding.ActivityPaymentBinding

@AndroidEntryPoint
class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment)
        setupNavigation()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_content)
        NavigationUI.setupWithNavController(binding.navView, navHostFragment!!.findNavController())
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}