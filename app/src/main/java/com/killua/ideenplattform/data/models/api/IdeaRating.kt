package com.killua.ideenplattform.data.models.api

import com.killua.ideenplattform.data.models.local.UserCaching

//    The rating information of the idea.
data class IdeaRating(
    val user: UserCaching,
//    example: 4.25
    val rating: Int
//    example: 4
)