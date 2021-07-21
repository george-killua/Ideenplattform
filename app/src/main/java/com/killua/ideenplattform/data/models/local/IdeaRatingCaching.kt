package com.killua.ideenplattform.data.models.local

import com.killua.ideenplattform.data.models.api.IdeaRating

//    The rating information of the idea.
data class IdeaRatingCaching(
    val user: UserCaching,
//    example: 4.25
    val rating: Int
//    example: 4
) {
    companion object {
        fun arrayFromIdeaRatings(list: List<IdeaRating>): Array<IdeaRatingCaching> {
            val cachesArray: ArrayList<IdeaRatingCaching> = arrayListOf()
            list.forEach { ideaRating ->
                cachesArray.add(
                    IdeaRatingCaching(
                        user = UserCaching.fromUser(ideaRating.user)!!,
                        rating =ideaRating.rating,
                    )
                )
            }
            return cachesArray.toTypedArray()
        }

        fun fromIdeaRating(ideaRating: IdeaRating) : IdeaRatingCaching {
            return IdeaRatingCaching(
                user = UserCaching.fromUser(ideaRating.user)!!,
                rating = ideaRating.rating,
            )
        }


    }
}