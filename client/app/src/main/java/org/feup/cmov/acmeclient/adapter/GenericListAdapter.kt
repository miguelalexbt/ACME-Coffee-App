package org.feup.cmov.acmeclient.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class GenericListAdapter<T, B : ViewDataBinding>(
    private val binder: (inflater: LayoutInflater, parent: ViewGroup) -> B,
    private val onBind: ((binding: ViewDataBinding, item: Content<T>) -> Unit)?
) : ListAdapter<Content<T>, RecyclerView.ViewHolder>(ListItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            binder(LayoutInflater.from(parent.context), parent),
            onBind
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder<T, B>).bind(getItem(position))
    }

    class ItemViewHolder<T, B: ViewDataBinding>(
        private val binding: B,
        private val onBind: ((binding: ViewDataBinding, content: Content<T>) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contentItem: Content<T>) = onBind?.invoke(binding, contentItem)
    }
}