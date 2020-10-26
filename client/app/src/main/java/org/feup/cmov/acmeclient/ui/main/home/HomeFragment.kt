package org.feup.cmov.acmeclient.ui.main.home

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ListAdapter
import android.widget.TextView
import androidx.core.view.ViewPropertyAnimatorListenerAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.adapter.ItemListAdapter
import org.feup.cmov.acmeclient.databinding.FragmentHomeBinding
import org.feup.cmov.acmeclient.databinding.FragmentSignInBinding
import org.feup.cmov.acmeclient.ui.auth.signin.SignInViewModel

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
        binding.viewModel = viewModel

        val adapter = ItemListAdapter()
        binding.homeRecyclerView.adapter = adapter
        subscribeUi(adapter)

//        setHasOptionsMenu(false)

        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)

//        val textView: TextView = view.findViewById(R.id.text_home)
//        viewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
//    }

    private fun subscribeUi(adapter: ItemListAdapter) {
        viewModel.items.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
        }
    }
}