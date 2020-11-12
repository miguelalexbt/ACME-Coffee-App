package org.feup.cmov.acmeclient.ui.auth.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.data.event.EventObserver
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

        subscribeUi()

        return binding.root
    }

    private fun subscribeUi() {
        viewModel.authState.observe(viewLifecycleOwner, {
            val authState = it ?: return@observe
            if (authState) {
                findNavController().navigate(R.id.action_signIn_to_main)
                activity?.finish()
            }
        })

        viewModel.uiEvent.observe(viewLifecycleOwner, EventObserver {
            binding.isLoading = it.isLoading

            if (it.error != null) Toast.makeText(context, it.error, Toast.LENGTH_LONG).show()
        })

        viewModel.formState.observe(viewLifecycleOwner, {
            val formState = it ?: return@observe

            binding.signInUsernameLayout.error = if (formState.usernameError != null)
                getString(formState.usernameError)
            else
                null

            binding.signInPasswordLayout.error = if (formState.passwordError != null)
                getString(formState.passwordError)
            else
                null
        })

        // Sign in
        binding.signInSubmit.setOnClickListener {
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