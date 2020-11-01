package org.feup.cmov.acmeclient.ui.main.vouchers

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.adapter.ItemListAdapter
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.databinding.FragmentHomeBinding
import org.feup.cmov.acmeclient.databinding.FragmentVouchersBinding
import org.feup.cmov.acmeclient.ui.main.home.HomeViewModel

@AndroidEntryPoint
class VouchersFragment : Fragment() {

    private lateinit var binding: FragmentVouchersBinding

//    private val viewModel: VouchersViewModel by viewModels()

    private val viewModel: HomeViewModel by activityViewModels() // viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVouchersBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        subscribeUi()

        return binding.root
    }

    private fun subscribeUi() {
        viewModel.items.observe(viewLifecycleOwner) {
            val items = it ?: return@observe

            println("VOUCHERS $items")
        }
    }
}