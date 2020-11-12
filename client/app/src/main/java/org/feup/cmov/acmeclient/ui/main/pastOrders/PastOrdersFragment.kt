package org.feup.cmov.acmeclient.ui.main.pastOrders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.adapter.GenericListAdapter
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.data.event.EventObserver
import org.feup.cmov.acmeclient.databinding.FragmentPastOrdersBinding
import org.feup.cmov.acmeclient.databinding.PastOrdersListItemBinding

@AndroidEntryPoint
class PastOrdersFragment : Fragment() {

    private lateinit var binding: FragmentPastOrdersBinding

    private val viewModel: PastOrdersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPastOrdersBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        val adapter = GenericListAdapter<PastOrderView, PastOrdersListItemBinding>(
            { adapterInflater, parent ->
                PastOrdersListItemBinding.inflate(adapterInflater, parent, false)
            },
            { binding, pastOrderContent ->
                (binding as PastOrdersListItemBinding).apply {
                    pastOrderView = pastOrderContent.content
                    orderViewReceipt.setOnClickListener {
                        ReceiptDialogFragment(pastOrderContent.content).show(
                            requireActivity().supportFragmentManager,
                            ReceiptDialogFragment.TAG
                        )
                    }
                    executePendingBindings()
                }
            }
        )

        binding.pastOrdersRecyclerView.adapter = adapter
        binding.pastOrdersRefreshLayout.setOnRefreshListener { viewModel.fetchPastOrders() }
        binding.pastOrdersRefreshLayoutEmpty.setOnRefreshListener { viewModel.fetchPastOrders() }

        subscribeUi(adapter)

        return binding.root
    }

    private fun subscribeUi(adapter: GenericListAdapter<PastOrderView, PastOrdersListItemBinding>) {
        viewModel.uiEvent.observe(viewLifecycleOwner, EventObserver {
            if (it.error != null) Toast.makeText(context, it.error, Toast.LENGTH_LONG).show()
        })

        viewModel.refreshing.observe(viewLifecycleOwner, {
            val refreshing = it ?: return@observe
            binding.pastOrdersRefreshLayout.isRefreshing = refreshing
            binding.pastOrdersRefreshLayoutEmpty.isRefreshing = refreshing
        })

        viewModel.pastOrders.observe(viewLifecycleOwner, {
            val pastOrders = it ?: return@observe

            binding.hasOrders = !pastOrders.data.isNullOrEmpty()
            binding.pastOrdersRefreshLayout.isRefreshing = pastOrders.status == Status.LOADING
            binding.pastOrdersRefreshLayoutEmpty.isRefreshing = pastOrders.status == Status.LOADING

            if (pastOrders.status == Status.SUCCESS)
                adapter.submitList(pastOrders.data)
        })
    }
}