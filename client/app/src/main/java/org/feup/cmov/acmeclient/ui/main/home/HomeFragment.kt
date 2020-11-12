package org.feup.cmov.acmeclient.ui.main.home

import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.SearchView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.adapter.GenericListAdapter
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.databinding.FragmentHomeBinding
import org.feup.cmov.acmeclient.databinding.HomeListItemBinding
import org.feup.cmov.acmeclient.utils.WEB_SERVICE_URL

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by activityViewModels()

    private var isViewCartVisible: Boolean = false

    private lateinit var filterBottomSheet: FilterBottomDialogFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        // Setup filter bottom sheet
        filterBottomSheet = FilterBottomDialogFragment()
        binding.filterButton.setOnClickListener {
            binding.searchBox.clearFocus()
            filterBottomSheet.show(
                requireActivity().supportFragmentManager,
                FilterBottomDialogFragment.TAG
            )
        }

        binding.signOutButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sign out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.signOut()
                    findNavController().navigate(R.id.action_homeFragment_to_authActivity)
                    activity?.finish()
                }
                .setNegativeButton("No") { _, _ -> }
                .show()
        }

        val adapter = GenericListAdapter<ItemView, HomeListItemBinding>(
            { adapterInflater, parent ->
                HomeListItemBinding.inflate(adapterInflater, parent, false)
            },
            { binding, itemContent ->
                (binding as HomeListItemBinding).apply {
                    item = itemContent.content

                    Picasso.get()
                        .load(WEB_SERVICE_URL + "/image/" + itemContent.content.name)
//                    .placeholder(R.drawable.logo)
                        .error(R.drawable.ic_baseline_image_not_supported_24)
                        .fit()
                        .centerCrop()
                        .into(listItemImage);

                    itemCard.setOnClickListener {
                        this@HomeFragment.binding.searchBox.clearFocus()
                        lifecycleScope.launch(Dispatchers.IO) {
                            val item = itemContent.content
                            withContext(Dispatchers.Main) {
                                ItemDialogFragment(item).show(
                                    requireActivity().supportFragmentManager,
                                    ItemDialogFragment.TAG
                                )
                            }
                        }
                    }

                    listItemFavorite.setOnClickListener {
                        viewModel.toggleFavoriteItem(itemContent.content)
                    }

                    executePendingBindings()
                }
            }
        )

        binding.homeRecyclerView.adapter = adapter
        binding.homeRefreshLayout.setOnRefreshListener { viewModel.fetchItems() }

        binding.viewCartFlowLayout.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_checkoutFragment)
        }

        subscribeUi(adapter)
        watchSearchBox()
        watchFilters()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.searchBox.clearFocus()
    }

    private fun animationViewCart(show: Boolean) {
        if (isViewCartVisible == show)
            return

        isViewCartVisible = show
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.viewCartFlowLayout)

        if (show)
            constraintSet.connect(
                R.id.view_cart_flow,
                ConstraintSet.TOP,
                R.id.view_cart_flow_layout,
                ConstraintSet.TOP,
                0
            )
        else
            constraintSet.connect(
                R.id.view_cart_flow,
                ConstraintSet.TOP,
                R.id.view_cart_flow_layout,
                ConstraintSet.BOTTOM,
                0
            )
        val transition = ChangeBounds()
        transition.interpolator = DecelerateInterpolator()
        transition.duration = 500
        TransitionManager.beginDelayedTransition(binding.homeConstraintLayout, transition)
        constraintSet.applyTo(binding.viewCartFlowLayout)
    }

    private fun subscribeUi(adapter: GenericListAdapter<ItemView, HomeListItemBinding>) {
        viewModel.refreshing.observe(viewLifecycleOwner, {
            val refreshing = it ?: return@observe
            binding.homeRefreshLayout.isRefreshing = refreshing
        })

        viewModel.cartState.observe(viewLifecycleOwner, {
            val cartState = it ?: return@observe

            binding.cartPrice = cartState.price.toFloat()
            binding.cartItemsCount = cartState.itemsCount

            animationViewCart(cartState.show)
        })

        viewModel.itemsQuery.observe(viewLifecycleOwner) {
            val items = it ?: return@observe

            binding.homeRefreshLayout.isRefreshing = items.status == Status.LOADING

            if (items.status == Status.SUCCESS)
                adapter.submitList(items.data)
        }
    }

    private fun watchSearchBox() {
        binding.searchBox.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                p0?.let {
                    viewModel.setQuery(it)
                }

                // Action was handled by listener so returns true
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0 == "") {
                    viewModel.setQuery("")
                }

                return true
            }

        })
    }

    private fun watchFilters() {
        viewModel.areFiltersActive.observe(viewLifecycleOwner) {
            binding.areFiltersActive = it
        }
    }
}