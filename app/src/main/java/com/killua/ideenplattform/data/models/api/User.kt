package com.killua.ideenplattform.data.models.api

data class User(
    val id: String
    //example: 490151ea-e5bf-4f98-9337-fa4d451d7175
    , val email: String,
    //example: max.mustermann@example.org
    val password: String,
    val firstname: String,
    //example: Max
    val lastname: String,
    //example: Mustermann
    val profilePicture: String,
    //example: https://ideenmanagement.tailored-apps.com/image/profile/some-url.png
     val isManager: Boolean
)

