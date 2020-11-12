package org.feup.cmov.acmeterminal.ui.order

import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeterminal.MainApplication.Companion.context
import org.feup.cmov.acmeterminal.R
import org.feup.cmov.acmeterminal.adapter.Content
import org.feup.cmov.acmeterminal.adapter.GenericListAdapter
import org.feup.cmov.acmeterminal.data.Resource
import org.feup.cmov.acmeterminal.data.api.SubmitOrderResponse
import org.feup.cmov.acmeterminal.databinding.ActivityOrderBinding
import org.feup.cmov.acmeterminal.databinding.OrderListVoucherBinding


@AndroidEntryPoint
class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order)

        // Ignore NFC
        NfcAdapter.getDefaultAdapter(context).setNdefPushMessage(null, this)

        val type = object : TypeToken<Resource<SubmitOrderResponse>>() {}.type
        val order: Resource<SubmitOrderResponse> = Gson().fromJson(
            intent.getStringExtra("ORDER"),
            type
        )

        println(order)

        val o = order.data!!

        supportActionBar?.title = "Order #${o.number}"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val adapter = GenericListAdapter<VoucherView, OrderListVoucherBinding>(
            { adapterInflater, parent ->
                OrderListVoucherBinding.inflate(adapterInflater, parent, false)
            },
            { binding, item ->
                val voucherBinding = binding as OrderListVoucherBinding
                voucherBinding.voucher = item.content
            }
        )

        binding.orderVouchersRecyclerView.adapter = adapter
        binding.total = o.total
        binding.hasVouchers = order.data.vouchers.isNotEmpty()
        binding.dismiss.setOnClickListener {
            finish()
        }

        adapter.submitList(o.vouchers.map {
            Content(
                View.generateViewId().toString(),
                VoucherView(it)
            )
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}