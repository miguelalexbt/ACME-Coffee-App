package org.feup.cmov.acmeclient.ui.main.vouchers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.adapter.ClickListener
import org.feup.cmov.acmeclient.adapter.ItemListAdapter
import org.feup.cmov.acmeclient.data.model.Item
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

        val adapter = ItemListAdapter(object: ClickListener<Item> {
            override fun onClick(item: Item) {
                print("CLICKED")
            }
        })

        binding.vouchersRecyclerView.adapter = adapter
//        binding.homeRefreshLayout.setOnRefreshListener { viewModel.fetchItems() }

        subscribeUi()

        return binding.root
    }

    private fun subscribeUi() {
//        viewModel.items.observe(viewLifecycleOwner) {
//            val items = it ?: return@observe
//
//            println("VOUCHERS $items")
//        }
    }
}