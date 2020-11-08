package org.feup.cmov.acmeclient.ui.main.checkout

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okio.Buffer
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.adapter.ClickListener
import org.feup.cmov.acmeclient.adapter.GenericListAdapter
import org.feup.cmov.acmeclient.adapter.ItemListAdapter
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.data.model.Voucher
import org.feup.cmov.acmeclient.databinding.CheckoutListItemBinding
import org.feup.cmov.acmeclient.databinding.CheckoutListVoucherBinding
import org.feup.cmov.acmeclient.databinding.FragmentCheckoutBinding
import org.feup.cmov.acmeclient.utils.Cache
import org.feup.cmov.acmeclient.utils.Crypto

@AndroidEntryPoint
class CheckoutFragment : Fragment(), NfcAdapter.OnNdefPushCompleteCallback {

    private lateinit var binding: FragmentCheckoutBinding

    private val viewModel: CheckoutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        val itemAdapter = GenericListAdapter<ItemView, CheckoutListItemBinding>(
            { adapterInflater, parent ->
                CheckoutListItemBinding.inflate(adapterInflater, parent, false)
            },
            { binding, item ->
                val itemBinding = binding as CheckoutListItemBinding
                itemBinding.item = item.content
            }
        )

        val voucherAdapter = GenericListAdapter<VoucherView, CheckoutListVoucherBinding>(
            { adapterInflater, parent ->
                CheckoutListVoucherBinding.inflate(adapterInflater, parent, false)
            },
            { binding, item ->
                val voucherBinding = binding as CheckoutListVoucherBinding
                voucherBinding.voucher = item.content
            }
        )

        binding.checkoutItemsRecyclerView.adapter = itemAdapter
        binding.checkoutVouchersRecyclerView.adapter = voucherAdapter

        subscribeUi(itemAdapter, voucherAdapter)

        binding.checkoutSubmit.setOnClickListener {
//            sendNdefMessage()
            findNavController().navigate(R.id.action_checkoutFragment_to_paymentActivity)
        }

        return binding.root
    }

    private fun subscribeUi(
        itemAdapter: GenericListAdapter<ItemView, CheckoutListItemBinding>,
        voucherAdapter: GenericListAdapter<VoucherView, CheckoutListVoucherBinding>
    ) {
        viewModel.isLoading.observe(viewLifecycleOwner, {
            val isLoading = it ?: return@observe

            binding.isLoading = isLoading
        })

        viewModel.items.observe(viewLifecycleOwner, {
            val items = it ?: return@observe

            if (items.status == Status.SUCCESS) {
                binding.hasItems = items.data!!.isNotEmpty()
                itemAdapter.submitList(items.data)
            }
        })

        viewModel.vouchers.observe(viewLifecycleOwner, {
            val vouchers = it ?: return@observe

            if (vouchers.status == Status.SUCCESS) {
                binding.hasVouchers = vouchers.data!!.isNotEmpty()
                voucherAdapter.submitList(vouchers.data)
            }
        })

        viewModel.total.observe(viewLifecycleOwner, {
            val total = it ?: return@observe

            if (total.status == Status.SUCCESS)
                binding.total = total.data
        })
    }

//    fun startNfcTransfer() {
        // Make screen bright
//        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//        sendNdefMessage()
//    }

    private fun sendNdefMessage() {
//        binding.order ?: return

        // Check for available NFC Adapter
        val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        if (nfcAdapter == null) {
            Toast.makeText(
                context,
                "NFC is not available on this device.",
                Toast.LENGTH_LONG
            ).show()
            // Nothing to do - go back to checkout
            return
        }
        if (!nfcAdapter?.isEnabled!!) {
            Toast.makeText(
                context,
                "NFC disabled on this device. Turn on to proceed",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val orderString = "abc" //generateOrderString()

        println("NFC")
        println(orderString)

        // Create NDEF message
        val msg = NdefMessage(createMimeRecord(orderString.toByteArray()))

        // Register a NDEF message to be sent in P2P
        println(nfcAdapter.isNdefPushEnabled)
        nfcAdapter.setOnNdefPushCompleteCallback(this, activity)
        nfcAdapter.setNdefPushMessage(msg, activity)
    }

    private fun createMimeRecord(payload: ByteArray?): NdefRecord? {
//        val mimeBytes = "application/org.feup.cmov.acmeclient".toByteArray(Charset.forName("ISO-8859-1"))
        return NdefRecord.createMime("application/org.feup.cmov.acmeclient", payload)
//        return NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, ByteArray(0), payload)
    }

    override fun onNdefPushComplete(p0: NfcEvent?) {
        Toast.makeText(context, "Message sent.", Toast.LENGTH_LONG).show()
    }
}