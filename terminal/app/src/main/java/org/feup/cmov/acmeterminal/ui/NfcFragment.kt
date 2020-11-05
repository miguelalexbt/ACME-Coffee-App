package org.feup.cmov.acmeterminal.ui

import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import org.feup.cmov.acmeterminal.R
import org.feup.cmov.acmeterminal.databinding.FragmentNfcBinding

@Suppress("DEPRECATION")
class NfcFragment : Fragment() {

    private lateinit var binding: FragmentNfcBinding

    private val viewModel: QrViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNfcBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }
}