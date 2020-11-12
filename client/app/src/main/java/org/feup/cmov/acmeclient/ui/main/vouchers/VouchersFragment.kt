package org.feup.cmov.acmeclient.ui.main.vouchers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.adapter.GenericListAdapter
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.data.model.Voucher
import org.feup.cmov.acmeclient.databinding.FragmentVouchersBinding
import org.feup.cmov.acmeclient.databinding.VouchersListItemBinding

@AndroidEntryPoint
class VouchersFragment : Fragment() {

    private lateinit var binding: FragmentVouchersBinding

    private val viewModel: VouchersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVouchersBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        val adapter = GenericListAdapter<VoucherView, VouchersListItemBinding>(
            { adapterInflater, parent ->
                VouchersListItemBinding.inflate(adapterInflater, parent, false)
            },
            { binding, voucherContent ->
                (binding as VouchersListItemBinding).apply {
                    voucher = voucherContent.content

                    Picasso.get()
                        .load("https://upload.wikimedia.org/wikipedia/commons/thumb/5/5e/Acme_Markets_lolo.svg/2560px-Acme_Markets_lolo.svg.png")
                        .placeholder(org.feup.cmov.acmeclient.R.drawable.ic_baseline_home_24)
                        .error(org.feup.cmov.acmeclient.R.drawable.ic_baseline_remove_circle_outline_24)
                        .fit()
                        .centerCrop()
                        .into(listItemImage);

                    listItemAdd.setOnClickListener {
                        viewModel.saveVoucherToOrder(voucherContent.content)
                    }

                    executePendingBindings()
                }
            }
        )

        binding.vouchersRecyclerView.adapter = adapter
        binding.vouchersRefreshLayout.setOnRefreshListener { viewModel.fetchVouchers() }
        binding.vouchersRefreshLayoutEmpty.setOnRefreshListener { viewModel.fetchVouchers() }

        subscribeUi(adapter)

        return binding.root
    }

    private fun subscribeUi(adapter: GenericListAdapter<VoucherView, VouchersListItemBinding>) {
        viewModel.refreshing.observe(viewLifecycleOwner, {
            val refreshing = it ?: return@observe
            binding.vouchersRefreshLayout.isRefreshing = refreshing
            binding.vouchersRefreshLayoutEmpty.isRefreshing = refreshing
        })

        viewModel.vouchers.observe(viewLifecycleOwner, {
            val vouchers = it ?: return@observe

            binding.hasVouchers = !vouchers.data.isNullOrEmpty()
            binding.vouchersRefreshLayout.isRefreshing = vouchers.status == Status.LOADING
            binding.vouchersRefreshLayoutEmpty.isRefreshing = vouchers.status == Status.LOADING

            if (vouchers.status == Status.SUCCESS)
                adapter.submitList(vouchers.data)
        })
    }
}