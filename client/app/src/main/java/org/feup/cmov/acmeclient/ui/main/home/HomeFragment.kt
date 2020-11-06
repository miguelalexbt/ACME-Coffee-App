package org.feup.cmov.acmeclient.ui.main.home

import android.animation.ObjectAnimator
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.adapter.ClickListener
import org.feup.cmov.acmeclient.adapter.ItemListAdapter
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.databinding.FragmentHomeBinding


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    private var isViewCartVisible: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        val adapter = ItemListAdapter(
            object : ClickListener<Item> {
                override fun onClick(target: Item) {
                    val previousQuantity = viewModel.getItemQuantity(target.id)
                    ItemDialogFragment(target, previousQuantity) { item, quantity ->
                        viewModel.saveItemToOrder(item, quantity)
                    }.show(requireActivity().supportFragmentManager, ItemDialogFragment.TAG)
                }
            }
        )

        binding.homeRecyclerView.adapter = adapter
        binding.homeRefreshLayout.setOnRefreshListener { viewModel.fetchItems() }

        subscribeUi(adapter)
        observeOrder()

        return binding.root
    }

    private fun showCartAnimation() {
        if (!isViewCartVisible)
            animationViewCart(true)
    }

    private fun concealCartAnimation() {
        if (isViewCartVisible)
            animationViewCart(false)
    }

    private fun animationViewCart(show: Boolean) {
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

    private fun subscribeUi(adapter: ItemListAdapter) {
        viewModel.refreshing.observe(viewLifecycleOwner, {
            val refreshing = it ?: return@observe
            binding.homeRefreshLayout.isRefreshing = refreshing
        })

        viewModel.items.observe(viewLifecycleOwner) {
            val items = it ?: return@observe

            binding.homeRefreshLayout.isRefreshing = items.status == Status.LOADING

            if (items.status == Status.SUCCESS)
                adapter.submitList(items.data)
        }
    }

    private fun observeOrder() {
        viewModel.order.observe(viewLifecycleOwner) {
            it ?: return@observe

            binding.cartPrice = 103.99f
            binding.cartItemsCount = it.items.values.sum()

            // TODO MICHAEL VERIFY PLS
            if (it.items.values.all { it == 0 })
                concealCartAnimation()
            else
                showCartAnimation()
        }
    }

}