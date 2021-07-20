package com.killua.ideenplattform.ui.home

import androidx.recyclerview.widget.RecyclerView
import com.killua.ideenplattform.data.models.api.Idea
import com.killua.ideenplattform.databinding.RvIdeeItemBinding

class IdeaViewHolder(val binding: RvIdeeItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Idea) {
        binding.data = item
        binding.executePendingBindings()
    }
}