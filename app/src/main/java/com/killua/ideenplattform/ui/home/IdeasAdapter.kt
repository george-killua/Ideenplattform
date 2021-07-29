package com.killua.ideenplattform.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.killua.ideenplattform.data.models.api.Idea
import com.killua.ideenplattform.databinding.ItemIdeaBinding

class IdeasAdapter(private val ideaOnClick: IdeaOnClick) :
    ListAdapter<Idea, IdeaViewHolder>(object : DiffUtil.ItemCallback<Idea>() {
        override fun areItemsTheSame(oldItem: Idea, newItem: Idea): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Idea, newItem: Idea): Boolean =
            oldItem.id == newItem.id
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IdeaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemIdeaBinding.inflate(layoutInflater, parent, false)
        return IdeaViewHolder(binding, ideaOnClick)
    }

    override fun onBindViewHolder(holder: IdeaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
