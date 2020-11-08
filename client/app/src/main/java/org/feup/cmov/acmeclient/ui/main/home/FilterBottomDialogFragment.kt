package org.feup.cmov.acmeclient.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.databinding.HomeFilterBottomSheetBinding

@AndroidEntryPoint
class FilterBottomDialogFragment() : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "FilterBottomSheet"
    }

    private lateinit var binding: HomeFilterBottomSheetBinding

    private val viewModel: HomeViewModel by viewModels()

//    private var selectedCategories: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeFilterBottomSheetBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        viewModel.categories.observe(viewLifecycleOwner, {
            val categories = it ?: return@observe
            //Todo
        })

        for (index in 0..3) {
            val chip =
                inflater.inflate(R.layout.filter_chip, binding.filterChipGroup, false) as Chip
            chip.id = index
            chip.text = "Chip $index"

//            chip.setOnCheckedChangeListener { p0, p1 ->
//                println(p0?.text)
//                println(p1)
//
//                categoriesMap[p0?.text as String] = p1
//
//                println(categoriesMap)
//            }

            binding.filterChipGroup.invalidate()
            binding.filterChipGroup.addView(chip)
        }

        binding.filterApplyButton.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        for (index in 0..3) {
//            val chip = inflater.inflate(R.layout.layout_chip_choice, chipGpRow, false) as Chip
//            chip.text = ("Chip 1")
//            chipGpRow.addView(chip)
//
//            val chip = Chip(binding.filterChipGroup.context)
////            chip.text= "Item ${tags[index]}"
//            chip.text= "Item $index"
////            chip.
//
//            // necessary to get single selection working
//            chip.isClickable = true
//            chip.isCheckable = true
////            chip.checkedIconTint = resources.getDrawable(R.drawable.checkbox_selector, )
//            binding.filterChipGroup.addView(chip)
//        }

//        val checkedChipId = chipGroup.checkedChipId // Returns View.NO_ID if singleSelection = false
//        val checkedChipIds = chipGroup.checkedChipIds // Returns a list of the selected chips' IDs, if any
//

//        binding.filterChipGroup.setOnCheckedChangeListener { group, checkedId ->
//            // Responds to child chip checked/unchecked
//            println("hello?")
//            println(binding.filterChipGroup.checkedChipIds)
//        }

    }
}