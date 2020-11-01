package org.feup.cmov.acmeclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.databinding.ListItemBinding
import org.feup.cmov.acmeclient.ui.main.home.HomeViewModel

interface ClickListener {
    fun onItemClick(item: Item)
}

class ItemListAdapter(
    private val listener: ClickListener
) : ListAdapter<HomeViewModel.MarkedItem, RecyclerView.ViewHolder>(ListItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bind(getItem(position), listener)
    }

    class ItemViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(contentItem: HomeViewModel.MarkedItem, listener: ClickListener) {

            binding.apply {
                item = contentItem.item
                isChosen = contentItem.chosen

//                Picasso.get().setIndicatorsEnabled(true)
                Picasso.get()
                    .load("https://upload.wikimedia.org/wikipedia/commons/thumb/5/5e/Acme_Markets_lolo.svg/2560px-Acme_Markets_lolo.svg.png")
                    .placeholder(R.drawable.ic_baseline_home_24)
                    .error(R.drawable.ic_baseline_remove_circle_outline_24)
                    .fit()
                    .centerCrop()
                    .into(listItemImage);

                executePendingBindings()

                listItemAdd.setOnClickListener {
                    listener.onItemClick(contentItem.item)
                }
            }
        }
    }
}

private class ListItemDiffCallback : DiffUtil.ItemCallback<HomeViewModel.MarkedItem>() {

    override fun areItemsTheSame(oldItem: HomeViewModel.MarkedItem, newItem: HomeViewModel.MarkedItem): Boolean {
        return oldItem.item.id == newItem.item.id
    }

    override fun areContentsTheSame(oldItem: HomeViewModel.MarkedItem, newItem: HomeViewModel.MarkedItem): Boolean {
        return oldItem.chosen == newItem.chosen
    }
}