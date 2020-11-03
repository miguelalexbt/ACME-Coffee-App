package org.feup.cmov.acmeclient.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.feup.cmov.acmeclient.data.model.Voucher
import org.feup.cmov.acmeclient.databinding.VouchersListItemBinding

class VoucherListAdapter(
    private val listener: ClickListener<Content<Voucher>>
) : ListAdapter<Content<Voucher>, RecyclerView.ViewHolder>(ListItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            VouchersListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bind(getItem(position), listener)
    }

    class ItemViewHolder(
        private val binding: VouchersListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contentVoucher: Content<Voucher>, listener: ClickListener<Content<Voucher>>) {
            binding.apply {
                voucher = contentVoucher.content
                isChosen = contentVoucher.isChosen

                Picasso.get()
                    .load("https://upload.wikimedia.org/wikipedia/commons/thumb/5/5e/Acme_Markets_lolo.svg/2560px-Acme_Markets_lolo.svg.png")
                    .placeholder(org.feup.cmov.acmeclient.R.drawable.ic_baseline_home_24)
                    .error(org.feup.cmov.acmeclient.R.drawable.ic_baseline_remove_circle_outline_24)
                    .fit()
                    .centerCrop()
                    .into(listItemImage);

                executePendingBindings()

                listItemAdd.setOnClickListener {
                    listener.onClick(contentVoucher)
                }
            }
        }
    }
}
