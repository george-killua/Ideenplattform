package com.killua.ideenplattform.ideamain.details

import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.killua.ideenplattform.R
import com.killua.ideenplattform.data.models.api.IdeaComment
import com.killua.ideenplattform.data.models.local.UserCaching
import com.killua.ideenplattform.databinding.ItemCommentBinding


class CommentsViewHolder(
    val binding: ItemCommentBinding,
    private val deleteFromAdapter: DeleteFromAdapter
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(currentUser: UserCaching, item: IdeaComment) {
        binding.comment = item
        binding.user = currentUser
        binding.option.setOnClickListener {
            val popup = PopupMenu(it.context, it)

            popup.inflate(R.menu.comment_menu)

            popup.setOnMenuItemClickListener {
                if (it.itemId == R.id.delete_comment) {

                    deleteFromAdapter.removeComment(item.id)
                    return@setOnMenuItemClickListener true
                }
                return@setOnMenuItemClickListener false
            }
            popup.show()
        }
    }
}


interface DeleteFromAdapter {
    fun removeComment(ideaId: String)
}
