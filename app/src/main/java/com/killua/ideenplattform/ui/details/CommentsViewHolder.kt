package com.killua.ideenplattform.ui.details

import androidx.recyclerview.widget.RecyclerView
import com.killua.ideenplattform.data.models.api.IdeaComment
import com.killua.ideenplattform.data.models.local.UserCaching
import com.killua.ideenplattform.databinding.ItemCommentBinding

import android.view.MenuItem
import android.widget.PopupMenu
import com.killua.ideenplattform.R


class CommentsViewHolder(val binding: ItemCommentBinding):RecyclerView.ViewHolder(binding.root){
    fun bind(currentUser: UserCaching, item: IdeaComment) {
        binding.comment=item
        binding.user=currentUser
        binding.option.setOnClickListener {
            val popup = PopupMenu(it.context, it)
            //inflating menu from xml resource
            //inflating menu from xml resource
            popup.inflate(R.menu.comment_menu)
            //adding click listener
            //adding click listener
       /*     popup.setOnMenuItemClickListener {
          //      if (it==)

            }*/
            //displaying the popup
            //displaying the popup
            popup.show()
        }
    }

}
