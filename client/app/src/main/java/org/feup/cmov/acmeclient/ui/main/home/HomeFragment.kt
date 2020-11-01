package org.feup.cmov.acmeclient.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.adapter.ClickListener
import org.feup.cmov.acmeclient.adapter.ItemListAdapter
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.databinding.FragmentHomeBinding

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        val adapter = ItemListAdapter(object : ClickListener {
            override fun onItemClick(item: Item) = viewModel.toggleItem(item)
        })

        binding.homeRecyclerView.adapter = adapter
        binding.homeRefreshLayout.setOnRefreshListener { viewModel.fetchItems() }

        subscribeUi(adapter)

        return binding.root
    }

    private fun subscribeUi(adapter: ItemListAdapter) {
        viewModel.refreshing.observe(viewLifecycleOwner, {
            val refreshing = it ?: return@observe
            binding.homeRefreshLayout.isRefreshing = refreshing
        })

        viewModel.items.observe(viewLifecycleOwner) {
            val items = it ?: return@observe

            binding.homeRefreshLayout.isRefreshing = items.status == Status.LOADING

            if (items.status == Status.SUCCESS)
                adapter.submitList(items.data)
        }
    }
}