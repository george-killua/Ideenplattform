package com.killua.ideenplattform.data.models.api

import com.killua.ideenplattform.data.models.local.UserCaching

data class IdeaComment(
    val id: String,
    //  example: 5cdbccd5-d909-4880-bf9b-0c33bc0e1a82
    val user: UserCaching,
    val message: String,
    // example: Really cool idea!
    //  The message of this comment

    val created: String
    //($yyyy-MM-dd'T'HH:mm:ss.SSSZ)
    // example: 2021-07-16T12:44:59.658+0000

)