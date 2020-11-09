package org.feup.cmov.acmeclient.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.databinding.NumberPickerDialogBinding

@AndroidEntryPoint
class ItemDialogFragment(
    private val dialogItem: ItemView,
    private val initialItemQuantity: Int,
    private val saveOperation: (item: ItemView, quantity: Int) -> Unit
) : DialogFragment() {

    companion object {
        const val TAG = "ItemDialog"
    }

    private lateinit var binding: NumberPickerDialogBinding

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
        binding.numberPicker.value = initialItemQuantity
        binding.numberPicker.wrapSelectorWheel = false;
        setupClickListeners()
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
            saveOperation(dialogItem, binding.numberPicker.value)
            dismiss()
        }
    }

}