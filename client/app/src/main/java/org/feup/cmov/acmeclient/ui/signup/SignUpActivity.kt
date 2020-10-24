//package org.feup.cmov.acmeclient.ui.signup
//
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.widget.Button
//import android.widget.EditText
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.Observer
//import androidx.lifecycle.SavedStateViewModelFactory
//import androidx.lifecycle.ViewModelProvider
//import dagger.hilt.android.AndroidEntryPoint
//import org.feup.cmov.acmeclient.R
//
//@AndroidEntryPoint
//class SignUpActivity : AppCompatActivity() {
//
//    private val viewModel : SignUpViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_sign_up)
//
//        val name = findViewById<EditText>(R.id.name)
//        val username = findViewById<EditText>(R.id.username)
//        val password = findViewById<EditText>(R.id.password)
//        val signUp = findViewById<Button>(R.id.sign_up)
//
//        // Update form state
////        name.afterTextChanged {
////            viewModel.updateFormData(
////                name.text.toString(), username.text.toString(), password.text.toString()
////            )
////        }
////
////        username.afterTextChanged {
////            viewModel.updateFormData(
////                name.text.toString(), username.text.toString(), password.text.toString()
////            )
////        }
////
////        password.afterTextChanged {
////            viewModel.updateFormData(
////                name.text.toString(), username.text.toString(), password.text.toString()
////            )
////        }
//
//        // Sign up
//        signUp.setOnClickListener {
//            viewModel.signUp(
//                name.text.toString(),
//                username.text.toString(),
//                password.text.toString()
//            )
//        }
//    }
//}
//
//fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
//    this.addTextChangedListener(object : TextWatcher {
//        override fun afterTextChanged(editable: Editable?) {
//            afterTextChanged.invoke(editable.toString())
//        }
//
//        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
//
//        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
//    })
//}
