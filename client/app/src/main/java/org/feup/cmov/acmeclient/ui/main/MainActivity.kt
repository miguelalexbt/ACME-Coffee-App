package org.feup.cmov.acmeclient.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.R

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setupNavigation()
    }

    private fun setupNavigation() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_content)
        NavigationUI.setupWithNavController(navView, navHostFragment!!.findNavController())

//        navView.setOnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.home -> {
//                    Toast.makeText(this, "Home selected", Toast.LENGTH_SHORT).show()
//                    true
//                }
//                R.id.vouchers -> {
//                    Toast.makeText(this, "Vouchers selected", Toast.LENGTH_SHORT).show()
//                    true
//                }
//                R.id.pastOrders -> {
//                    Toast.makeText(this, "Past Orders selected", Toast.LENGTH_SHORT).show()
//                    true
//                }
//                R.id.checkout -> {
//                    Toast.makeText(this, "Checkout selected", Toast.LENGTH_SHORT).show()
//                    true
//                }
//                else -> true
//            }
//        }
    }
}