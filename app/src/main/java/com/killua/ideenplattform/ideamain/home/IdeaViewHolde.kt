package com.killua.ideenplattform.ideamain.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.github.pgreze.reactions.PopupGravity
import com.github.pgreze.reactions.ReactionSelectedListener
import com.github.pgreze.reactions.dsl.reactionPopup
import com.github.pgreze.reactions.dsl.reactions
import com.killua.ideenplattform.R
import com.killua.ideenplattform.data.models.api.Idea
import com.killua.ideenplattform.data.models.local.UserCaching
import com.killua.ideenplattform.databinding.ItemIdeaBinding

class IdeaViewHolder(
    val binding: ItemIdeaBinding,
    private val ideaOnClick: IdeaOnClick,
    private val currentUser: UserCaching,
) :
    RecyclerView.ViewHolder(binding.root) {
    private val context = binding.root.context


    private val popup = reactionPopup(context) {
        reactions {
            resId { R.drawable.ic_amour_emoji }
            resId { R.drawable.ic_chuckle_emoji }
            resId { R.drawable.ic_fun_giggles_emoji }
            resId { R.drawable.ic_embarrassed }
            resId { R.drawable.ic_angry_emoji }

        }
        popupGravity = PopupGravity.DEFAULT
        popupMargin = context.resources.getDimensionPixelSize(R.dimen._35sdp)
        val cornerSizeInDp = 35
        popupCornerRadius =
            TypedValue.applyDimension(COMPLEX_UNIT_DIP, cornerSizeInDp.toFloat(), context.resources.displayMetrics).toInt()
        popupColor = Color.WHITE

        reactionTexts = R.array.rating_data
        popupCornerRadius = 40
        popupColor = Color.LTGRAY
        popupAlpha = 255
        reactionSize = context.resources.getDimensionPixelSize(R.dimen._35sdp)
        horizontalMargin = context.resources.getDimensionPixelSize(R.dimen._4sdp)
        verticalMargin = horizontalMargin / 2
    }


    @SuppressLint("ClickableViewAccessibility")
    fun bind(item: Idea) {
        binding.tvComment.setOnClickListener { ideaOnClick.clicked(item.id) }
        val didILiked = item.ratings.firstOrNull { it.user == currentUser }
        val currentRate = didILiked?.rating ?: -2
        updateLikeImage(currentRate)

        popup.reactionSelectedListener = object : ReactionSelectedListener {
            override fun invoke(position: Int): Boolean = true.also {
                likeButtonSetUp(item, position + 1)

            }
        }
        binding.rate.setOnTouchListener { v, event ->
            // Resolve reactions selection
            popup.onTouch(v, event)
        }




        binding.data = item
        binding.executePendingBindings()

    }

    private fun likeButtonSetUp(idea: Idea, emojiTag: Int = -1) {
        val didILiked = idea.ratings.firstOrNull { it.user == currentUser }
        var currentRate = didILiked?.rating ?: -2
        if (emojiTag < 0) {
            binding.rate.setImageDrawable(AppCompatResources.getDrawable(context,
                R.drawable.ic_like))

                ideaOnClick.emojiOnClickInsert(emojiTag, idea.id)

        } else if (emojiTag == currentRate) {
          ideaOnClick.emojiOnClickRemove(idea.id)
            currentRate=-2


        } else if (emojiTag > 0) {
          ideaOnClick.emojiOnClickInsert(emojiTag, idea.id)
            currentRate = emojiTag
        } else
            return
        updateLikeImage(currentRate)


    }

    private fun updateLikeImage(rate: Int) {
        binding.rate.setImageDrawable(when (rate) {
            1 -> AppCompatResources.getDrawable(context, R.drawable.ic_amour_emoji)
            2 -> AppCompatResources.getDrawable(context, R.drawable.ic_chuckle_emoji)
            3 -> AppCompatResources.getDrawable(context, R.drawable.ic_fun_giggles_emoji)
            4 -> AppCompatResources.getDrawable(context, R.drawable.ic_embarrassed)
            5 -> AppCompatResources.getDrawable(context, R.drawable.ic_angry_emoji)
            else -> AppCompatResources.getDrawable(context, R.drawable.ic_like)
        })
    }


}


interface IdeaOnClick {
    fun clicked(ideaId: String)
    fun emojiOnClickInsert(i: Int, id: String)
    fun emojiOnClickRemove(id: String)
}

