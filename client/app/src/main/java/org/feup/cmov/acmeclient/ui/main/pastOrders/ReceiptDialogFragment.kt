package org.feup.cmov.acmeclient.ui.main.pastOrders

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
import org.feup.cmov.acmeclient.data.model.Receipt
import org.feup.cmov.acmeclient.databinding.NumberPickerDialogBinding

@AndroidEntryPoint
class ReceiptDialogFragment(
    private val orderId: String
) : DialogFragment() {

    companion object {
        const val TAG = "ReceiptDialog"
    }

    private lateinit var binding: NumberPickerDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

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
//        binding.numberPicker.value = initialItemQuantity
//        binding.numberPicker.wrapSelectorWheel = false;
//        setupClickListeners()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

//    private fun setupClickListeners() {
//        binding.itemDialogCancelButton.setOnClickListener {
//            dismiss()
//        }
//
//        binding.itemDialogSaveButton.setOnClickListener {
//            saveOperation(dialogItem, binding.numberPicker.value)
//            dismiss()
//        }
//    }

}