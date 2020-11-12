package org.feup.cmov.acmeclient.ui.splashscreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.ui.auth.AuthActivity
import org.feup.cmov.acmeclient.ui.auth.signin.SignInViewModel
import org.feup.cmov.acmeclient.ui.main.MainActivity
import org.feup.cmov.acmeclient.utils.Cache

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private val splashTime = 1000L

    private val viewModel: SplashScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        viewModel.authState.observe(this, {
            val authState = it ?: return@observe
            Handler().postDelayed(Runnable {
                startActivity(
                    Intent(
                        this@SplashScreenActivity,
                        if (authState) MainActivity::class.java else AuthActivity::class.java
                    )
                )
                finish()
            }, splashTime)
        })
    }
}