package com.killua.ideenplattform.data.models.api

//    The rating information of the idea.
data class IdeaRating(
    val user: User,
//    example: 4.25
    val count: Double
//    example: 4
)