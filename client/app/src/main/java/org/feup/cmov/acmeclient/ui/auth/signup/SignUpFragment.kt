package org.feup.cmov.acmeclient.ui.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.data.event.EventObserver
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

        ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_popup_item,
            List(12) { (it + 1).toString() }
        )
            .also {
                binding.signUpCcMonth.setAdapter(it)
            }

        ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_popup_item,
            List(5) { "20" + (20 + it).toString() }
        )
            .also {
                binding.signUpCcYear.setAdapter(it)
            }

        subscribeUi()

        return binding.root
    }

    private fun subscribeUi() {
        viewModel.authState.observe(viewLifecycleOwner, {
            val authState = it ?: return@observe
            if (authState) {
                findNavController().navigate(R.id.action_signUp_to_main)
                activity?.finish()
            }
        })

        viewModel.uiEvent.observe(viewLifecycleOwner, EventObserver {
            binding.isLoading = it.isLoading

            if (it.error != null) Toast.makeText(context, it.error, Toast.LENGTH_LONG).show()
        })

        viewModel.formState.observe(viewLifecycleOwner, {
            val formState = it ?: return@observe

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

            binding.signUpCcCvvLayout.error = if (formState.ccCVVError != null)
                getString(formState.ccCVVError)
            else
                null

            binding.signUpCcMonthLayout.error = if (formState.ccExpirationMonthError != null)
                getString(formState.ccExpirationMonthError)
            else
                null

            binding.signUpCcYearLayout.error = if (formState.ccExpirationYearError != null)
                getString(formState.ccExpirationYearError)
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

        // Sign up
        binding.signUpSubmit.setOnClickListener {
            viewModel.signUp(
                binding.signUpName.text.toString(),
                binding.signUpNif.text.toString(),
                binding.signUpCcNumber.text.toString(),
                binding.signUpCcCvv.text.toString(),
                binding.signUpCcMonth.text.toString(),
                binding.signUpCcYear.text.toString(),
                binding.signUpUsername.text.toString(),
                binding.signUpPassword.text.toString()
            )
        }

        // Redirect to sign in
        binding.signUpRedirect.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}