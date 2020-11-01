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
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okio.Buffer
import org.feup.cmov.acmeclient.MainApplication
import org.feup.cmov.acmeclient.databinding.FragmentCheckoutBinding
import org.feup.cmov.acmeclient.ui.main.home.HomeViewModel
import org.feup.cmov.acmeclient.utils.Cache
import org.feup.cmov.acmeclient.utils.Crypto
import java.nio.charset.Charset

@AndroidEntryPoint
class CheckoutFragment : Fragment(), NfcAdapter.OnNdefPushCompleteCallback {

    private lateinit var binding: FragmentCheckoutBinding

    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
//        binding.viewModel = viewModel

//        viewModel.items.observe(viewLifecycleOwner, Observer observe@{
//            val items = it ?: return@observe
//        })

        binding.showQrcode.setOnClickListener {
            showQRCode()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        val bitmap = viewModel.generateQRCode("Sample Text")
//        binding.imageViewQRCode.setImageBitmap(bitmap)

        // Display QRCode

    }

    fun showQRCode() {
        val bitmap = generateQRCode()
        binding.imageViewQRCode.setImageBitmap(bitmap)
    }

    private fun generateQRCode(): Bitmap {
        val width = 600
        val height = 600
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()

        val orderString = generateOrderString()

        try {
            val bitMatrix = codeWriter.encode(orderString, BarcodeFormat.QR_CODE, width, height)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        } catch (e: WriterException) {
            Log.d(ContentValues.TAG, "generateQRCode: ${e.message}")
        }
        return bitmap
    }

    fun startNfcTransfer() {
        // Make screen bright
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        sendNdefMessage()
    }

    private fun sendNdefMessage() {
        // Check for available NFC Adapter
        val nfcAdapter = NfcAdapter.getDefaultAdapter(MainApplication.context)
        if (nfcAdapter == null) {
            Toast.makeText(
                MainApplication.context,
                "NFC is not available on this device.",
                Toast.LENGTH_LONG
            ).show()
            // Nothing to do - go back to checkout
        }

        val orderString = generateOrderString()

        // Create NDEF message
        val msg = NdefMessage(createMimeRecord(orderString.toByteArray()))

        // Register a NDEF message to be sent in P2P
        nfcAdapter.setNdefPushMessage(msg, activity)
        nfcAdapter.setOnNdefPushCompleteCallback(this, activity)
    }

    private fun createMimeRecord(payload: ByteArray?): NdefRecord? {
        val mimeBytes = "text/json".toByteArray(Charset.forName("ISO-8859-1"))
        return NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, ByteArray(0), payload)
    }

    data class QRItem(
        val id: String,
        val quantity: Int
    )

    fun generateOrderString(): String {
//        val newList: List<QRItem> = itemList.map {
//            QRItem(it.id, 2)
//        }

        // List of Items to send
//        val order: MutableList<QRItem> = MutableList<QRItem>(10) {
//            QRItem("10", it)
//        }

        val order = runBlocking {
//            var order: String = ""
//            homeViewModel.order.first().forEach {
//                order += it.key + ":" + it.value + ";"
//            }
//            println(order)
//            return@runBlocking order

            ""
        }

        println(order)

        val gson = Gson()
        val orderString: String = gson.toJson(order)

        println(orderString)

        // Cached user
        val cachedUser = runBlocking {
            Cache.cachedUser.first()!!
        }

        val buffer = Buffer()
        // Add data to buffer
        buffer.write(cachedUser.userId.toByteArray())
            .write(orderString.toByteArray())

        val signature = Crypto.sign(cachedUser.username, buffer)

        buffer.close()

        val qr_string: String = "${cachedUser.userId}-$signature-$orderString}"

//        println(qr_string)

        return qr_string
    }

    override fun onNdefPushComplete(p0: NfcEvent?) {
        Toast.makeText(context, "Message sent.", Toast.LENGTH_LONG).show()
    }



}