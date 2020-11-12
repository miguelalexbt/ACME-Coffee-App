package org.feup.cmov.acmeclient.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.ui.auth.AuthActivity
import org.feup.cmov.acmeclient.ui.main.MainActivity

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private val splashTime = 1000L

    private val viewModel: SplashScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        viewModel.authState.observe(this, {
            val authState = it ?: return@observe
            Handler().postDelayed({
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