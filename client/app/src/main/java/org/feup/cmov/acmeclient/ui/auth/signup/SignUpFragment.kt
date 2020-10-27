package org.feup.cmov.acmeclient.ui.auth.signup

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
import org.feup.cmov.acmeclient.databinding.FragmentSignUpBinding

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding

    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
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
                findNavController().navigate(R.id.action_signUp_to_main)
        })
    }

    private fun subscribeUi() {
        viewModel.formState.observe(viewLifecycleOwner, {
            val formState = it ?: return@observe

            binding.signUpSubmit.isEnabled = formState.isValid

            binding.signUpNameLayout.error = if (formState.nameError != null)
                getString(formState.nameError)
            else
                null

            binding.signUpNifLayout.error = if (formState.nifError != null)
                getString(formState.nifError)
            else
                null

            binding.signUpCcNumberLayout.error = if (formState.ccNumberError != null)
                getString(formState.ccNumberError)
            else
                null

            binding.signUpCcExpirationLayout.error = if (formState.ccExpirationError != null)
                getString(formState.ccExpirationError)
            else
                null

            binding.signUpCcCvvLayout.error = if (formState.ccCVVError != null)
                getString(formState.ccCVVError)
            else
                null

            binding.signUpUsernameLayout.error = if (formState.usernameError != null)
                getString(formState.usernameError)
            else
                null

            binding.signUpPasswordLayout.error = if (formState.passwordError != null)
                getString(formState.passwordError)
            else
                null
        })

        binding.signUpNameLayout.editText?.doAfterTextChanged { text ->
            viewModel.checkPersonalInfo(text.toString())
        }

        binding.signUpNifLayout.editText?.doAfterTextChanged { text ->
            viewModel.checkBillingInfo(
                text.toString(),
                binding.signUpCcNumber.text.toString(),
                binding.signUpCcExpiration.text.toString(),
                binding.signUpCcCvv.text.toString()
            )
        }

        binding.signUpCcNumberLayout.editText?.doAfterTextChanged { text ->
            viewModel.checkBillingInfo(
                binding.signUpNif.text.toString(),
                text.toString(),
                binding.signUpCcExpiration.text.toString(),
                binding.signUpCcCvv.text.toString()
            )
        }

        binding.signUpCcExpirationLayout.editText?.doAfterTextChanged { text ->
            viewModel.checkBillingInfo(
                binding.signUpNif.text.toString(),
                binding.signUpCcNumber.text.toString(),
                text.toString(),
                binding.signUpCcCvv.text.toString()
            )
        }

        binding.signUpCcCvvLayout.editText?.doAfterTextChanged { text ->
            viewModel.checkBillingInfo(
                binding.signUpNif.text.toString(),
                binding.signUpCcNumber.text.toString(),
                binding.signUpCcExpiration.text.toString(),
                text.toString()
            )
        }

        binding.signUpUsernameLayout.editText?.doAfterTextChanged { text ->
            viewModel.checkCredentials(
                text.toString(),
                binding.signUpPassword.text.toString()
            )
        }

        binding.signUpPasswordLayout.editText?.doAfterTextChanged { text ->
            viewModel.checkCredentials(
                binding.signUpUsername.text.toString(),
                text.toString()
            )
        }

        // Sign up
        binding.signUpSubmit.setOnClickListener {
            binding.isLoading = true

//            viewModel.signUp(
//                binding.signInUsername.text.toString(),
//                binding.signInPassword.text.toString()
//            )
        }

        // Redirect to sign in
        binding.signUpRedirect.setOnClickListener {
            findNavController().navigate(R.id.action_signUp_to_signIn)
        }
    }
}