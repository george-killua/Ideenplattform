package com.killua.ideenplattform.ideamain.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.killua.ideenplattform.databinding.ItemSortLayoutBinding

class SortItemAdapter (private val sortItemOnClick: SortItemOnClick) :
    ListAdapter<String, SortItemViewHolder>(object :
        DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String,
        ): Boolean =
            oldItem.length == newItem.length
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SortItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSortLayoutBinding.inflate(layoutInflater, parent, false)
        return SortItemViewHolder(binding, sortItemOnClick)
    }

    override fun onBindViewHolder(holder: SortItemViewHolder, position: Int) {

        holder.bind(getItem(position))
    }
}

interface SortItemOnClick {
    fun itemClicked(ideaId: String)
}

class SortItemViewHolder   (val binding: ItemSortLayoutBinding,
private val sortedItemOnClick: SortItemOnClick
) :
RecyclerView.ViewHolder(binding.root) {


    fun bind(name: String) {

        binding.root.setOnClickListener { sortedItemOnClick.itemClicked(name) }
        binding.tvSortName.text = name
    }
}

