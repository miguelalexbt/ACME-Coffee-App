package org.feup.cmov.acmeclient.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.databinding.ListItemBinding

class ItemListAdapter : ListAdapter<Item, RecyclerView.ViewHolder>(ListItemDiffCallback()) {

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
        val item = getItem(position)
        (holder as ItemViewHolder).bind(item)
    }

    class ItemViewHolder(
        private val binding: ListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
//        init {
//            binding.setClickListener {
//                println("click")
//                binding.plant?.let { plant ->
//                    navigateToPlant(plant, it)
//                }
//            }
//        }

//        private fun navigateToPlant(
//            plant: Plant,
//            view: View
//        ) {
//            val direction =
//                HomeViewPagerFragmentDirections.actionViewPagerFragmentToPlantDetailFragment(
//                    plant.plantId
//                )
//            view.findNavController().navigate(direction)
//        }

        fun bind(item2: Item) {
            println(item2)
            binding.apply {
                item = item2
                executePendingBindings()
            }
        }
    }
}

private class ListItemDiffCallback : DiffUtil.ItemCallback<Item>() {

    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        TODO("SEE BETTER")
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        TODO("SEE BETTER")
        return oldItem.name == newItem.name
    }
}