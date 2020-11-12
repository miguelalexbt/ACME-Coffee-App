package org.feup.cmov.acmeclient.ui.payment

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.feup.cmov.acmeclient.databinding.FragmentQrBinding

@AndroidEntryPoint
class QrFragment : Fragment() {

    private lateinit var binding: FragmentQrBinding

    private val viewModel: PaymentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQrBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        generateQRCode()

        return binding.root
    }

    private fun generateQRCode() {
        binding.isLoading = true

        lifecycleScope.launch(Dispatchers.IO) {
            val orderString = viewModel.orderString.first()

            val width = 600
            val height = 600
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val codeWriter = MultiFormatWriter()

            try {
                val bitMatrix =
                    codeWriter.encode(orderString.utf8(), BarcodeFormat.QR_CODE, width, height)
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                    }
                }
            } catch (e: WriterException) {
                Log.d(ContentValues.TAG, "generateQRCode: ${e.message}")
            }

            withContext(Dispatchers.Main) {
                binding.isLoading = false
                binding.qrCodeImage.setImageBitmap(bitmap)
            }
        }
    }
}