package org.feup.cmov.acmeclient.ui.main.vouchers

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.action_bar.view.*
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.adapter.GenericListAdapter
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.data.event.EventObserver
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

        val actionBar = (activity as AppCompatActivity).supportActionBar
        val actionBarView = layoutInflater.inflate(R.layout.action_bar, null)
        actionBarView.title.text = getString(R.string.vouchers_title)
        actionBar?.setDisplayShowCustomEnabled(true)
        actionBar?.customView = actionBarView
        actionBar?.show()

        val adapter = GenericListAdapter<VoucherView, VouchersListItemBinding>(
            { adapterInflater, parent ->
                VouchersListItemBinding.inflate(adapterInflater, parent, false)
            },
            { binding, voucherContent ->
                (binding as VouchersListItemBinding).apply {
                    voucher = voucherContent.content

                    val icon = if (voucherContent.content.type == 'o')
                        R.drawable.offercoffee
                    else
                        R.drawable.discount

                    Picasso.get()
                        .load(icon)
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.ic_baseline_image_not_supported_24)
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
        viewModel.uiEvent.observe(viewLifecycleOwner, EventObserver {
            if (it.error != null) Toast.makeText(context, it.error, Toast.LENGTH_LONG).show()
        })

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