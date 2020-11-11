package org.feup.cmov.acmeclient.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.databinding.NumberPickerDialogBinding

@AndroidEntryPoint
class ItemDialogFragment(
    private val dialogItem: ItemView
) : DialogFragment() {

    companion object {
        const val TAG = "ItemDialog"
    }

    private lateinit var binding: NumberPickerDialogBinding

    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = NumberPickerDialogBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.numberPicker.wrapSelectorWheel = false;
        setupClickListeners()
        subscribeUi()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupClickListeners() {
        binding.itemDialogCancelButton.setOnClickListener {
            dismiss()
        }

        binding.itemDialogSaveButton.setOnClickListener {
            viewModel.saveItemToOrder(dialogItem, binding.numberPicker.value)
            dismiss()
        }
    }

    private fun subscribeUi() {
        viewModel.order.observe(viewLifecycleOwner, {
            val order = it ?: return@observe
            binding.numberPicker.value = order.items.getOrDefault(dialogItem.id, 0)
        })
    }
}