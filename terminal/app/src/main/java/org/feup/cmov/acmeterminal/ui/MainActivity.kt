package org.feup.cmov.acmeterminal.ui

import android.app.Activity
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.feup.cmov.acmeterminal.MainApplication.Companion.context
import org.feup.cmov.acmeterminal.R
import org.feup.cmov.acmeterminal.data.Status
import org.feup.cmov.acmeterminal.databinding.ActivityMainBinding
import org.feup.cmov.acmeterminal.ui.order.OrderActivity


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(context)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        supportActionBar?.hide()

        // Ignore NFC
        nfcAdapter?.setNdefPushMessage(null, this)

        binding.qrScan.setOnClickListener {
            scanQRCode()
        }
    }

    override fun onResume() {
        super.onResume()

        if (nfcAdapter != null)
            enableForegroundDispatch(this, nfcAdapter)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent != null)
            receiveMessageFromDevice(intent)
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
        val downloadDialog = MaterialAlertDialogBuilder(this)
            .setTitle("No Scanner Found")
            .setMessage("Download a scanner QRcode app?")
            .setPositiveButton("Yes") { _, _ ->
                val uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            .setNegativeButton("No", null)

        return downloadDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK)
                handleData(data?.getStringExtra("SCAN_RESULT"))
            else
                Toast.makeText(context, R.string.error_qr_code, Toast.LENGTH_LONG).show()
        }
    }

    private fun receiveMessageFromDevice(intent: Intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            val msg = rawMsgs!![0] as NdefMessage
            val messageString = String(msg.records[0].payload)

            handleData(messageString)
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

    private fun handleData(data: String?) {
        data ?: return

        lifecycleScope.launch {
            val order = viewModel.handleData(data)

            if (order.status == Status.SUCCESS) {
                val intent = Intent(this@MainActivity, OrderActivity::class.java)
                intent.putExtra("ORDER", Gson().toJson(order))
                startActivity(intent)
            } else if (order.status == Status.ERROR) {
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setView(layoutInflater.inflate(R.layout.order_error_dialog, null))
                    .setPositiveButton("Retry") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }
}