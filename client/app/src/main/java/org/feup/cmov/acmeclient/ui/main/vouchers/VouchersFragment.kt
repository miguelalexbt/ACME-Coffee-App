package org.feup.cmov.acmeclient.ui.main.vouchers

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.adapter.ItemListAdapter
import org.feup.cmov.acmeclient.databinding.FragmentHomeBinding
import org.feup.cmov.acmeclient.databinding.FragmentVouchersBinding
import org.feup.cmov.acmeclient.ui.main.home.HomeViewModel

@AndroidEntryPoint
class VouchersFragment : Fragment() {

//    private lateinit var vouchersViewModel: VouchersViewModel
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        vouchersViewModel =
//            ViewModelProvider(this).get(VouchersViewModel::class.java)
//        val root = inflater.inflate(R.layout.fragment_vouchers, container, false)
//        val textView: TextView = root.findViewById(R.id.text_vouchers)
//        vouchersViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
//        return root
//    }


    private lateinit var binding: FragmentVouchersBinding

    private val viewModel: VouchersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
//        val root = inflater.inflate(R.layout.fragment_home, container, false)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        binding = FragmentVouchersBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        return binding.root
    }
}