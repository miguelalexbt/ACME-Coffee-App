package org.feup.cmov.acmeterminal.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class ListItemDiffCallback<T> : DiffUtil.ItemCallback<Content<T>>() {

    override fun areItemsTheSame(oldItem: Content<T>, newItem: Content<T>): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Content<T>, newItem: Content<T>): Boolean {
        return oldItem.content == newItem.content
    }
}