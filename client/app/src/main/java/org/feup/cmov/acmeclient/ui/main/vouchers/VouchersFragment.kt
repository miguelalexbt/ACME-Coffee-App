package org.feup.cmov.acmeclient.ui.main.vouchers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.adapter.ClickListener
import org.feup.cmov.acmeclient.adapter.Content
import org.feup.cmov.acmeclient.adapter.VoucherListAdapter
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.data.model.Voucher
import org.feup.cmov.acmeclient.databinding.FragmentVouchersBinding

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

        val adapter = VoucherListAdapter(object: ClickListener<Content<Voucher>> {
            override fun onClick(content: Content<Voucher>) {
                viewModel.toggleVoucher(content)
            }
        })

        binding.vouchersRecyclerView.adapter = adapter
        binding.vouchersRefreshLayout.setOnRefreshListener { viewModel.fetchVouchers() }

        subscribeUi(adapter)

        return binding.root
    }

    private fun subscribeUi(adapter: VoucherListAdapter) {
        viewModel.refreshing.observe(viewLifecycleOwner, {
            val refreshing = it ?: return@observe
            binding.vouchersRefreshLayout.isRefreshing = refreshing
        })

        viewModel.vouchers.observe(viewLifecycleOwner, {
            val vouchers = it ?: return@observe

            binding.vouchersRefreshLayout.isRefreshing = vouchers.status == Status.LOADING

            if (vouchers.status == Status.SUCCESS)
                adapter.submitList(vouchers.data)
        })
    }
}