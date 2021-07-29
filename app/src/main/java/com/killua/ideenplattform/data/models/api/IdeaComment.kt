package com.killua.ideenplattform.data.models.api

import com.google.gson.annotations.SerializedName
import com.killua.ideenplattform.data.models.local.UserCaching

data class IdeaComment(
    val id: String,
    //  example: 5cdbccd5-d909-4880-bf9b-0c33bc0e1a82
    @SerializedName("author")
    val user: UserCaching,
    val message: String="",
    // example: Really cool idea!
    //  The message of this tv_comment

    val created: String
    //($yyyy-MM-dd'T'HH:mm:ss.SSSZ)
    // example: 2021-07-16T12:44:59.658+0000

) {

    fun ownerComment(currentUser: UserCaching)=currentUser.email == user.email
}