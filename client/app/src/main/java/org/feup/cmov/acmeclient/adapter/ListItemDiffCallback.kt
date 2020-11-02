package org.feup.cmov.acmeclient.adapter

import androidx.recyclerview.widget.DiffUtil

class ListItemDiffCallback<T> : DiffUtil.ItemCallback<Content<T>>() {

    override fun areItemsTheSame(oldItem: Content<T>, newItem: Content<T>): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Content<T>, newItem: Content<T>): Boolean {
        return oldItem.isChosen == newItem.isChosen
    }
}