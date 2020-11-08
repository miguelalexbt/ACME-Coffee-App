package org.feup.cmov.acmeclient.ui.payment

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.feup.cmov.acmeclient.MainApplication
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.databinding.ActivityPaymentBinding

@AndroidEntryPoint
class PaymentActivity : AppCompatActivity(), NfcAdapter.OnNdefPushCompleteCallback {

    private lateinit var binding: ActivityPaymentBinding

    private lateinit var nfcAdapter: NfcAdapter

    private val viewModel: PaymentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment)
        binding.lifecycleOwner = this

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        subscribeUi()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun subscribeUi() {
        binding.qrCode.setOnClickListener { generateQRCode() }
        binding.nfc.setOnClickListener { sendNdefMessage() }
    }

    private fun generateQRCode() {
        lifecycleScope.launch(Dispatchers.IO) {
            val orderString = viewModel.orderString.first()

            val width = 600
            val height = 600
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val codeWriter = MultiFormatWriter()

            try {
                val bitMatrix = codeWriter.encode(orderString.utf8(), BarcodeFormat.QR_CODE, width, height)
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                    }
                }
            } catch (e: WriterException) {
                Log.d(ContentValues.TAG, "generateQRCode: ${e.message}")
            }

            withContext(Dispatchers.Main) {
                binding.imageViewQRCode.setImageBitmap(bitmap)
            }
        }
    }

    private fun sendNdefMessage() {

        // Check if NFC is available
        if (nfcAdapter == null) {
            Toast.makeText(
                this, "NFC is not available on this device.", Toast.LENGTH_LONG
            ).show()
            return
        }

        // Check if NFC is enabled
        if (!nfcAdapter.isEnabled) {
            Toast.makeText(
                this, "NFC disabled on this device. Turn on to proceed", Toast.LENGTH_LONG
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
        nfcAdapter.setOnNdefPushCompleteCallback(this, this)
        nfcAdapter.setNdefPushMessage(msg, this)
    }

    private fun createMimeRecord(payload: ByteArray?): NdefRecord? {
        return NdefRecord.createMime("application/org.feup.cmov.acmeclient", payload)
    }

    override fun onNdefPushComplete(event: NfcEvent?) {
        Toast.makeText(this, "Message sent.", Toast.LENGTH_LONG).show()
    }
}