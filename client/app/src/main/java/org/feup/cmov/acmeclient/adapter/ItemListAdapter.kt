package org.feup.cmov.acmeclient.adapter

import android.app.Activity
import android.content.*
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import org.feup.cmov.acmeclient.MainApplication
import org.feup.cmov.acmeclient.R
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.databinding.HomeListItemBinding
import org.feup.cmov.acmeclient.utils.WEB_SERVICE_URL

class ItemListAdapter(
    private val cardListener: ClickListener<Item>
) : ListAdapter<Content<Item>, RecyclerView.ViewHolder>(ListItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            HomeListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            cardListener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bind(getItem(position))
    }

    class ItemViewHolder(
        private val binding: HomeListItemBinding,
        private val cardListener: ClickListener<Item>
    ) : RecyclerView.ViewHolder(binding.root) {

//        init {
//
//        }

        fun bind(contentItem: Content<Item>) {
            binding.apply {
                item = contentItem.content
                isChosen = contentItem.isChosen

                Picasso.get()
                    .load(WEB_SERVICE_URL + "/image/" + contentItem.content.name)
//                    .placeholder(R.drawable.logo)
                    .error(R.drawable.ic_baseline_image_not_supported_24)
                    .fit()
                    .centerCrop()
                    .into(listItemImage);

                setClickListener {
                    cardListener.onClick(contentItem.content)
                }

                executePendingBindings()
            }
        }
    }
}
