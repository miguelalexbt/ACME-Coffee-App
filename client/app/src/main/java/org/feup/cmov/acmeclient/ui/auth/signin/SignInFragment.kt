package org.feup.cmov.acmeclient.ui.auth.signin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        binding.viewModel = viewModel

        // Redirect if successful, show error otherwise
        viewModel.state.observe(viewLifecycleOwner, { newState ->
            if (newState.success)
                findNavController().navigate(R.id.action_signIn_to_main)

            if (newState.error != R.string.empty_string)
                Toast.makeText(context, newState.error, Toast.LENGTH_LONG).show()
        })

        // Redirect to sign up
        binding.signInRedirect.setOnClickListener {
            findNavController().navigate(R.id.action_signIn_to_signUp)
        }

        return binding.root
    }
}