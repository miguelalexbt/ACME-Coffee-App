package org.feup.cmov.acmeclient.ui.auth.signin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.databinding.FragmentSignInBinding

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding

    private val viewModel: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        subscribeApi()
        subscribeUi()

        return binding.root
    }

    private fun subscribeApi() {
        viewModel.apiState.observe(viewLifecycleOwner, {
            val apiState = it ?: return@observe

            binding.isLoading = false

            if (apiState.error != null)
                Toast.makeText(context, apiState.error, Toast.LENGTH_LONG).show()

            if (apiState.success)
                findNavController().navigate(R.id.action_signIn_to_main)
        })
    }

    private fun subscribeUi() {
        viewModel.formState.observe(viewLifecycleOwner, {
            val formState = it ?: return@observe

            binding.signInSubmit.isEnabled = formState.isValid

            binding.signInUsernameLayout.error = if (formState.usernameError != null)
                getString(formState.usernameError)
            else
                null

            binding.signInPasswordLayout.error = if (formState.passwordError != null)
                getString(formState.passwordError)
            else
                null
        })

        binding.signInUsernameLayout.editText?.doAfterTextChanged { text ->
            viewModel.checkUsername(text.toString())
        }

        binding.signInPasswordLayout.editText?.doAfterTextChanged { text ->
            viewModel.checkPassword(text.toString())
        }

        // Sign in
        binding.signInSubmit.setOnClickListener {
            binding.isLoading = true

            viewModel.signIn(
                binding.signInUsername.text.toString(),
                binding.signInPassword.text.toString()
            )
        }

        // Redirect to sign up
        binding.signInRedirect.setOnClickListener {
            findNavController().navigate(R.id.action_signIn_to_signUp)
        }
    }
}