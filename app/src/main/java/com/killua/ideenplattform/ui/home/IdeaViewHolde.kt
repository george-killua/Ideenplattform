package com.killua.ideenplattform.ui.home

import androidx.recyclerview.widget.RecyclerView
import com.killua.ideenplattform.data.models.api.Idea
import com.killua.ideenplattform.databinding.ItemIdeaBinding

class IdeaViewHolder(val binding: ItemIdeaBinding, private val ideaOnClick: IdeaOnClick) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Idea) {
        binding.root.setOnClickListener { ideaOnClick.clicked(item.id) }
        binding.data = item
        binding.executePendingBindings()
    }
}

interface IdeaOnClick {
    fun clicked(ideaId: String)
}