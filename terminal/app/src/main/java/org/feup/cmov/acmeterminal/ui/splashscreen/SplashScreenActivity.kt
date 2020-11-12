package org.feup.cmov.acmeterminal.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeterminal.R
import org.feup.cmov.acmeterminal.ui.MainActivity

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private val splashTime = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed(Runnable {
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            finish()
        }, splashTime)
    }
}