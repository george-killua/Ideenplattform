package com.killua.ideenplattform.ui.home

import androidx.recyclerview.widget.RecyclerView
import com.killua.ideenplattform.databinding.ItemCategoryBinding

class CategoryViewHolder(
    val binding: ItemCategoryBinding,
    private val categoryOnClick: CategoryOnClick,
) :
    RecyclerView.ViewHolder(binding.root) {


    fun bind(name: String, id: String) {

        binding.root.setOnClickListener { categoryOnClick.categoryClicked(id) }
        binding.tvName.text = name
    }
}

interface CategoryOnClick {
    fun categoryClicked(ideaId: String)
}