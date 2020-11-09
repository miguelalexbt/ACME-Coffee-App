package org.feup.cmov.acmeclient.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.databinding.HomeFilterBottomSheetBinding

@AndroidEntryPoint
class FilterBottomDialogFragment() : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "FilterBottomSheet"
    }

    private lateinit var binding: HomeFilterBottomSheetBinding

    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeFilterBottomSheetBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        viewModel.categories.observe(viewLifecycleOwner, {
            val categories = it ?: return@observe

            if (categories.status == Status.SUCCESS) {
                binding.filterChipGroup.removeAllViews()
                categories.data?.forEach { key, value ->
                    val chip = inflater.inflate(
                        R.layout.filter_chip,
                        binding.filterChipGroup,
                        false
                    ) as Chip
                    chip.id = View.generateViewId()
                    chip.text = key.capitalize()
                    chip.isChecked = value

                    binding.filterChipGroup.addView(chip)
                }
            }
        })

        binding.filterChipGroup.invalidate()

        binding.filterApplyButton.setOnClickListener {
            val categoriesSelected: List<String> =
                binding.filterChipGroup.children.filter {
                    (it as Chip).isChecked
                }.map {
                    (it as Chip).text.toString().toLowerCase()
                }.toList()

            viewModel.setCategoriesFilter(categoriesSelected)
            viewModel.setShowOnlyFavorites(binding.showOnlyFavoritesSwitch.isChecked)

            dismiss()
        }

        return binding.root
    }

}