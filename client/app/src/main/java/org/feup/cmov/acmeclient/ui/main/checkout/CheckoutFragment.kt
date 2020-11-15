package org.feup.cmov.acmeclient.ui.main.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.action_bar.view.*
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.adapter.GenericListAdapter
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.data.event.EventObserver
import org.feup.cmov.acmeclient.databinding.CheckoutListItemBinding
import org.feup.cmov.acmeclient.databinding.CheckoutListVoucherBinding
import org.feup.cmov.acmeclient.databinding.FragmentCheckoutBinding

@AndroidEntryPoint
class CheckoutFragment : Fragment() {

    private lateinit var binding: FragmentCheckoutBinding

    private val viewModel: CheckoutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        val actionBar = (activity as AppCompatActivity).supportActionBar
        val actionBarView = layoutInflater.inflate(R.layout.action_bar, null)
        actionBarView.title.text = getString(R.string.checkout_title)
        actionBar?.setDisplayShowCustomEnabled(true)
        actionBar?.customView = actionBarView
        actionBar?.show()

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
            findNavController().navigate(R.id.action_checkoutFragment_to_paymentActivity)
        }

        return binding.root
    }

    private fun subscribeUi(
        itemAdapter: GenericListAdapter<ItemView, CheckoutListItemBinding>,
        voucherAdapter: GenericListAdapter<VoucherView, CheckoutListVoucherBinding>
    ) {
        viewModel.uiEvent.observe(viewLifecycleOwner, EventObserver {
            if (it.error != null) Toast.makeText(context, it.error, Toast.LENGTH_LONG).show()
        })

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
}