package org.feup.cmov.acmeterminal.ui

import android.app.Activity
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeterminal.MainApplication.Companion.context
import org.feup.cmov.acmeterminal.R
import org.feup.cmov.acmeterminal.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(context)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        supportActionBar?.hide()

        // Ignore NFC
        nfcAdapter?.setNdefPushMessage(null, this)

        binding.qrScan.setOnClickListener {
            scanQRCode()
        }
    }

    override fun onResume() {
        super.onResume()

        if (nfcAdapter != null) {
            enableForegroundDispatch(this, nfcAdapter)
            receiveMessageFromDevice(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun scanQRCode() {
        // For testing purposes
//        val data = "5fa42ddbe729e501461d53cf:2#0b358717-0c20-405d-b056-b0afd2088793:JgBivAWMV9Y4mFHr89oEOvXVw25fiE0+w5h7/kbh/OD8TJbLAdiRaY5CaDvssAkJ7BAuYYUQUXLt9ZwWvw0kmQ=="
//        viewModel.handleData(data)
//        val data = "5fa48e61c0f2be001264d8eb:2;5fa48e61c0f2be001264d8ec:5;22cff775-bd93-4b10-baa7-97fe05214ab0;da8306e1-bdee-444f-ab17-a9de4486c062#7894f856-eb6c-4057-8b20-3db0221fc273:dZlXk5Sy/g7h9enwreDiZ3SH4bPT4D1OzGwbGwT46QJmbJ20f9e3FFF8mSdIiSG+sZJHwmQK24I19hTL2Mruvg=="
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
        val downloadDialog = AlertDialog.Builder(this)
        downloadDialog.setTitle("No Scanner Found")
        downloadDialog.setMessage("Download a scanner QRcode app?")

        downloadDialog.setPositiveButton("Yes") { _, _ ->
            val uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        downloadDialog.setNegativeButton("No", null)

        return downloadDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK)
                viewModel.handleData(data?.getStringExtra("SCAN_RESULT"))
            else
                Toast.makeText(context, R.string.error_qr_code, Toast.LENGTH_LONG).show()
        }
    }

    private fun receiveMessageFromDevice(intent: Intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            val msg = rawMsgs!![0] as NdefMessage
            val messageString = String(msg.records[0].payload)

            viewModel.handleData(messageString)
//            println(messageString)
//            Toast.makeText(context, messageString, Toast.LENGTH_LONG).show()
        }
    }

    private fun enableForegroundDispatch(activity: AppCompatActivity, adapter: NfcAdapter?) {
        val intent = Intent(activity.applicationContext, activity.javaClass)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(activity.applicationContext, 0, intent, 0)

        val filters = arrayOfNulls<IntentFilter>(1)
        val techList = arrayOf<Array<String>>()

        filters[0] = IntentFilter()
        with(filters[0]) {
            this?.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED)
            this?.addCategory(Intent.CATEGORY_DEFAULT)
            try {
                this?.addDataType("application/org.feup.cmov.acmeclient")
            } catch (ex: IntentFilter.MalformedMimeTypeException) {
                throw RuntimeException("Check your MIME type")
            }
        }

        adapter?.enableForegroundDispatch(activity, pendingIntent, filters, techList)
    }
}