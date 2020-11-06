package org.feup.cmov.acmeterminal.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import org.feup.cmov.acmeterminal.databinding.FragmentNfcBinding

@Suppress("DEPRECATION")
class NfcFragment : Fragment() {

    private lateinit var binding: FragmentNfcBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNfcBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    // TODO passar tudo para um fragment???
}