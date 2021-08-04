package com.killua.ideenplattform.ideamain.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.killua.ideenplattform.data.models.local.CategoryCaching
import com.killua.ideenplattform.databinding.ItemCategoryBinding
import java.util.*

class CategoryAdapter(private val categoryOnClick: CategoryOnClick) :
    ListAdapter<CategoryCaching, CategoryViewHolder>(object :
        DiffUtil.ItemCallback<CategoryCaching>() {
        override fun areItemsTheSame(oldItem: CategoryCaching, newItem: CategoryCaching): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(
            oldItem: CategoryCaching,
            newItem: CategoryCaching,
        ): Boolean =
            oldItem.id == newItem.id
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCategoryBinding.inflate(layoutInflater, parent, false)
        return CategoryViewHolder(binding, categoryOnClick)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        var item = getItem(position)

        val lang = Locale.getDefault().displayLanguage
        holder.bind(if (lang == Locale.ENGLISH.language) item.name_en
        else item.name_de, item.id)
    }
}