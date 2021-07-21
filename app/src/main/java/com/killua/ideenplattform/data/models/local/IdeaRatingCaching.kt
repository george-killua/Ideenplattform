package com.killua.ideenplattform.data.models.local

//    The rating information of the idea.
data class IdeaRatingCaching(
    val user: UserCaching,
//    example: 4.25
    val rating: Int
//    example: 4
)
