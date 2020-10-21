package org.feup.cmov.acmeclient.ui.signup

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.R

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private val viewModel : SignUpViewModel by viewModels()
//        factoryProducer = { SavedStateViewModelFactory(application, this) }
//    )

//    private val viewModel : SignUpViewModel = ViewModelProvider(
//        this, SavedStateViewModelFactory(application, this)
//    )[SignUpViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val name = findViewById<EditText>(R.id.name)
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val signUp = findViewById<Button>(R.id.sign_up)
        
        viewModel.check()

//        viewModel.user.observe(this@SignUpActivity, Observer {
//            val result = it ?: return@Observer
//
//            finish()
//        })
    }
}
