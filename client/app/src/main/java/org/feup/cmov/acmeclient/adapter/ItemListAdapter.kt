package org.feup.cmov.acmeclient.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.databinding.HomeListItemBinding

class ItemListAdapter(
    private val listener: ClickListener<Content<Item>>
) : ListAdapter<Content<Item>, RecyclerView.ViewHolder>(ListItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            HomeListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bind(getItem(position), listener)
    }

    class ItemViewHolder(
        private val binding: HomeListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contentItem: Content<Item>, listener: ClickListener<Content<Item>>) {
            binding.apply {
                item = contentItem.content
                isChosen = contentItem.isChosen

                Picasso.get()
                    .load("https://upload.wikimedia.org/wikipedia/commons/thumb/5/5e/Acme_Markets_lolo.svg/2560px-Acme_Markets_lolo.svg.png")
                    .placeholder(org.feup.cmov.acmeclient.R.drawable.ic_baseline_home_24)
                    .error(org.feup.cmov.acmeclient.R.drawable.ic_baseline_remove_circle_outline_24)
                    .fit()
                    .centerCrop()
                    .into(listItemImage);

                executePendingBindings()

                listItemAdd.setOnClickListener {
                    listener.onClick(contentItem)
                }
            }
        }
    }
}
