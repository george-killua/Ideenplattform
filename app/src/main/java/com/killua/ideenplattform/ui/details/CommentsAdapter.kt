package com.killua.ideenplattform.ui.details

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.killua.ideenplattform.data.models.api.IdeaComment
import org.w3c.dom.Comment

class CommentsAdapter :ListAdapter<IdeaComment,CommentsViewHolder>(object:
    DiffUtil.ItemCallback<IdeaComment>() {
    override fun areItemsTheSame(oldItem: IdeaComment, newItem: IdeaComment)=oldItem==newItem
    override fun areContentsTheSame(oldItem: IdeaComment, newItem: IdeaComment)=oldItem.id==newItem.id

}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}