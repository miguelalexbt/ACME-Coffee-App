package org.feup.cmov.acmeclient.ui.main.pastOrders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.subscribe
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.adapter.GenericListAdapter
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.data.model.PastOrder
import org.feup.cmov.acmeclient.data.model.Receipt
import org.feup.cmov.acmeclient.databinding.CheckoutListItemBinding
import org.feup.cmov.acmeclient.databinding.CheckoutListVoucherBinding
import org.feup.cmov.acmeclient.databinding.ReceiptDialogBinding
import org.feup.cmov.acmeclient.ui.main.checkout.ItemView
import org.feup.cmov.acmeclient.ui.main.checkout.VoucherView
import org.feup.cmov.acmeclient.ui.main.home.HomeViewModel

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
        binding = ReceiptDialogBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.total = pastOrder.total
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.numberPicker.value = initialItemQuantity
//        binding.numberPicker.wrapSelectorWheel = false;
//        setupClickListeners()
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

//        viewModel.isLoading.observe(viewLifecycleOwner, {
//            val isLoading = it ?: return@observe
//
//            binding.isLoading = isLoading
//        })

        viewModel.getOrderItems(pastOrder.items).observe(viewLifecycleOwner, {
            val items = it ?: return@observe

            if (items.status == Status.SUCCESS)
                itemAdapter.submitList(items.data)
        })

        viewModel.getOrderVouchers(pastOrder.vouchers).observe(viewLifecycleOwner, {
            val vouchers = it ?: return@observe

            if (vouchers.status == Status.SUCCESS) {
                binding.hasVouchers = vouchers.data!!.isNotEmpty()
                voucherAdapter.submitList(vouchers.data)
            }
        })
//
//        viewModel.total.observe(viewLifecycleOwner, {
//            val total = it ?: return@observe
//
//            if (total.status == Status.SUCCESS)
//                binding.total = total.data
//        })
    }

//    private fun setupClickListeners() {
//        binding.itemDialogCancelButton.setOnClickListener {
//            dismiss()
//        }
//
//        binding.itemDialogSaveButton.setOnClickListener {
//            saveOperation(dialogItem, binding.numberPicker.value)
//            dismiss()
//        }
//    }

}