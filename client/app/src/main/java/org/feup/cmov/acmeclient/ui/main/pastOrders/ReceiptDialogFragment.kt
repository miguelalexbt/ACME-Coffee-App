package org.feup.cmov.acmeclient.ui.main.pastOrders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.adapter.Content
import org.feup.cmov.acmeclient.adapter.GenericListAdapter
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.databinding.CheckoutListItemBinding
import org.feup.cmov.acmeclient.databinding.CheckoutListVoucherBinding
import org.feup.cmov.acmeclient.databinding.ReceiptDialogBinding
import org.feup.cmov.acmeclient.ui.main.checkout.ItemView
import org.feup.cmov.acmeclient.ui.main.checkout.VoucherView

@AndroidEntryPoint
class ReceiptDialogFragment(
    private val pastOrder: PastOrderView
) : DialogFragment() {

    companion object {
        const val TAG = "ReceiptDialog"
    }

    private lateinit var binding: ReceiptDialogBinding

    private val viewModel: PastOrdersViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.attributes?.windowAnimations = R.style.FullScreenDialogStyleAnim

        binding = ReceiptDialogBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.total = pastOrder.total
        binding.orderNumber = pastOrder.number
        binding.hasVouchers = pastOrder.vouchers.isNotEmpty()

        binding.closeReceiptButton.setOnClickListener {
            dismiss()
        }

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

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    private fun subscribeUi(
        itemAdapter: GenericListAdapter<ItemView, CheckoutListItemBinding>,
        voucherAdapter: GenericListAdapter<VoucherView, CheckoutListVoucherBinding>
    ) {

        viewModel.getOrderReceipt(pastOrder.id).observe(viewLifecycleOwner, {
            val receipt = it ?: return@observe
            if (receipt.status == Status.SUCCESS) {
                binding.nif = receipt.data!!.nif
                binding.ccNumberLastFour = receipt.data.ccNumber.takeLast(4)
            }
        })

        viewModel.getOrderItems(pastOrder.items).observe(viewLifecycleOwner, {
            val items = it ?: return@observe

            if (items.status == Status.SUCCESS)
                itemAdapter.submitList(items.data)
        })

        voucherAdapter.submitList(pastOrder.vouchers.map {
            Content(it, VoucherView(it.toCharArray()[0]))
        })
    }

}