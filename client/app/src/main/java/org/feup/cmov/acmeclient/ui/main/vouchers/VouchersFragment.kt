package org.feup.cmov.acmeclient.ui.main.vouchers

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import org.feup.cmov.acmeclient.R

class VouchersFragment : Fragment() {

    private lateinit var vouchersViewModel: VouchersViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vouchersViewModel =
            ViewModelProvider(this).get(VouchersViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_vouchers, container, false)
        val textView: TextView = root.findViewById(R.id.text_vouchers)
        vouchersViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}