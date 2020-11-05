package org.feup.cmov.acmeterminal.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeterminal.R
import org.feup.cmov.acmeterminal.databinding.FragmentQrBinding

@AndroidEntryPoint
class QrFragment : Fragment() {

    private lateinit var binding: FragmentQrBinding

    private val viewModel: QrViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQrBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.qrScan.setOnClickListener {
            readQRCode()
        }

        return binding.root
    }

    private fun readQRCode() {
        // For testing purposes
//        val data = "5fa42ddbe729e501461d53cf:2#0b358717-0c20-405d-b056-b0afd2088793:JgBivAWMV9Y4mFHr89oEOvXVw25fiE0+w5h7/kbh/OD8TJbLAdiRaY5CaDvssAkJ7BAuYYUQUXLt9ZwWvw0kmQ=="
//        viewModel.handleData(data)

        try {
            val intent = Intent("com.google.zxing.client.android.SCAN")
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE")
            startActivityForResult(intent, 1)
        } catch (e: ActivityNotFoundException) {
            showDialog().show()
        }
    }

    private fun showDialog(): AlertDialog {
        val downloadDialog = AlertDialog.Builder(requireContext())
        downloadDialog.setTitle("No Scanner Found")
        downloadDialog.setMessage("Download a scanner QRcode app?")

        downloadDialog.setPositiveButton("Yes") { _, _ ->
            val uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            requireActivity().startActivity(intent)
        }

        downloadDialog.setNegativeButton("No", null)

        return downloadDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK)
                viewModel.handleData(data?.getStringExtra("SCAN_RESULT"))
            else
                Toast.makeText(context, R.string.error_qr_code, Toast.LENGTH_LONG).show()
        }
    }
}