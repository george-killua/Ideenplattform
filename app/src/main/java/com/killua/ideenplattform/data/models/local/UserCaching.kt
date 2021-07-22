package com.killua.ideenplattform.data.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user_table")
data class UserCaching(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    val userId: String = ""
    //example: 490151ea-e5bf-4f98-9337-fa4d451d7175
    , val email: String,
    //example: max.mustermann@example.org

    val firstname: String,
    //example: Max
    val lastname: String,
    //example: Mustermann
    val profilePicture: String,
    //example: https://ideenmanagement.tailored-apps.com/image/profile/some-url.png
    val isManager: Boolean
)
