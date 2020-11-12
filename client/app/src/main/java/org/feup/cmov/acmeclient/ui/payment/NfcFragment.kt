package org.feup.cmov.acmeclient.ui.payment

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.databinding.FragmentNfcBinding

@Suppress("DEPRECATION")
@AndroidEntryPoint
class NfcFragment : Fragment() {

    private lateinit var binding: FragmentNfcBinding

    private val viewModel: PaymentViewModel by activityViewModels()
    private val nfcAdapter: NfcAdapter? by lazy { NfcAdapter.getDefaultAdapter(context) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNfcBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

//        nfcAdapter?.setOnNdefPushCompleteCallback({
//            viewModel.clearOrder()
//            activity?.finish()
//        }, activity)

        viewModel.setNfcStatus(nfcAdapter?.isEnabled ?: false)

        subscribeUi()

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

    private fun subscribeUi() {
        if (nfcAdapter == null) {
            binding.nfcDescription.text = getString(R.string.nfc_not_available)
            return
        }

        viewModel.isNfcActive.observe(viewLifecycleOwner, {
            val isNfcActive = it ?: return@observe

            binding.nfcDescription.text = if (isNfcActive)
                getString(R.string.payment_nfc_description)
            else
                getString(R.string.nfc_disabled)
        })
    }

    private fun sendNdefMessage(cancel: Boolean = false) {
        if (cancel) {
            nfcAdapter?.setNdefPushMessage(null, activity)
            return
        }

        binding.isLoading = true

        lifecycleScope.launch(Dispatchers.IO) {
            val orderString = viewModel.orderString.first()

            // Create NDEF message
            val msg = NdefMessage(createMimeRecord(orderString.toByteArray()))

            // Register a NDEF message to be sent in P2P
            nfcAdapter?.setNdefPushMessage(msg, activity)
        }

        binding.isLoading = false
    }

    private fun createMimeRecord(payload: ByteArray?): NdefRecord? {
        return NdefRecord.createMime("application/org.feup.cmov.acmeclient", payload)
    }
}