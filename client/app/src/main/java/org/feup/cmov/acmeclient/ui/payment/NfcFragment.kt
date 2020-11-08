package org.feup.cmov.acmeclient.ui.payment

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.databinding.FragmentNfcBinding

@AndroidEntryPoint
class NfcFragment: Fragment() {

    private lateinit var binding: FragmentNfcBinding

    private val viewModel: PaymentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNfcBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        nfcAdapter?.setOnNdefPushCompleteCallback({
            println("WTD")
            Toast.makeText(context, "Message sent.", Toast.LENGTH_LONG).show()
        }, activity)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        sendNdefMessage(cancel = false)
    }

    override fun onPause() {
        super.onPause()
        sendNdefMessage(cancel = true)
    }

    private fun sendNdefMessage(cancel: Boolean = false) {
        binding.isLoading = true

        val nfcAdapter = NfcAdapter.getDefaultAdapter(context)

        // Check if NFC is available
        if (nfcAdapter == null) {
            Toast.makeText(context, R.string.nfc_not_available, Toast.LENGTH_LONG).show()
            return
        }

        // Check if NFC is enabled
        if (!nfcAdapter.isEnabled) {
            Toast.makeText(context, R.string.nfc_disabled, Toast.LENGTH_LONG).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val orderString = viewModel.orderString.first()

            println("NFC $orderString")

            // Create NDEF message
            val msg = if (!cancel)
                NdefMessage(createMimeRecord(orderString.toByteArray()))
            else
                null

            // Register a NDEF message to be sent in P2P
            nfcAdapter.setNdefPushMessage(msg, activity)
        }

        binding.isLoading = false
    }

    private fun createMimeRecord(payload: ByteArray?): NdefRecord? {
        return NdefRecord.createMime("application/org.feup.cmov.acmeclient", payload)
    }
}