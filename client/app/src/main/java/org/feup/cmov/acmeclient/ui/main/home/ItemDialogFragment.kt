package org.feup.cmov.acmeclient.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.databinding.NumberPickerDialogBinding

@AndroidEntryPoint
class ItemDialogFragment(
    private val dialogItem: Item?,
    private val numberPickerMethod: (item: Item?, quantity: Int) -> Unit
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
//        return inflater.inflate(R.layout.fragment_item_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.numberPicker.setOnValueChangedListener(NumberPicker.OnValueChangeListener { numberPicker, oldVal, newVal -> numberPickerMethod(dialogItem, newVal) })
        //        binding.item = dialogItem
//        setupView(view)
//        setupClickListeners(view)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

//    private fun setupView(view: View) {
//        view.tvTitle.text = arguments?.getString(KEY_TITLE)
//        view.tvSubTitle.text = arguments?.getString(KEY_SUBTITLE)
//    }

//    private fun setupClickListeners(view: View) {
//        view.btnPositive.setOnClickListener {
//            // TODO: Do some task here
//            dismiss()
//        }
//        view.btnNegative.setOnClickListener {
//            // TODO: Do some task here
//            dismiss()
//        }
//    }

}