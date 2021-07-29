package com.killua.ideenplattform.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.killua.ideenplattform.data.models.api.IdeaComment
import com.killua.ideenplattform.data.models.local.UserCaching
import com.killua.ideenplattform.databinding.ItemCommentBinding

class CommentsAdapter(val userCaching: UserCaching, private val deleteFromAdapter: DeleteFromAdapter) :
    ListAdapter<IdeaComment, CommentsViewHolder>(object :
        DiffUtil.ItemCallback<IdeaComment>() {
        override fun areItemsTheSame(oldItem: IdeaComment, newItem: IdeaComment) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: IdeaComment, newItem: IdeaComment) =
            oldItem.id == newItem.id

    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CommentsViewHolder(
            ItemCommentBinding.inflate(layoutInflater, parent, false),
            deleteFromAdapter
        )
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        holder.bind(userCaching, getItem(position))
    }
}