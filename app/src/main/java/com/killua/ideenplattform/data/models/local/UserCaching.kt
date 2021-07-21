package com.killua.ideenplattform.data.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.killua.ideenplattform.data.models.api.User

@Entity(tableName = "user_table")
data class UserCaching(
    @PrimaryKey(autoGenerate = false)
    val userId: String = ""
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
) {
    companion object {
        fun arrayFromUsers(list: List<User>?): ArrayList<UserCaching> {
            val cachesArray: ArrayList<UserCaching> = arrayListOf()
            list?.forEach { user ->
                cachesArray.add(
                    UserCaching(
                        userId = user.id,
                        email = user.email,
                        "",
                        firstname = user.firstname,
                        lastname = user.lastname,
                        profilePicture = user.profilePicture,
                        isManager = user.isManager
                    )
                )
            }
            return cachesArray
        }

        fun fromUser(user: User?) :UserCaching?{
            return if(user!=null)UserCaching(
                userId = user.id,
                email = user.email,
                "",
                firstname = user.firstname,
                lastname = user.lastname,
                profilePicture = user.profilePicture,
                isManager = user.isManager
            )
            else null
        }


    }
}

