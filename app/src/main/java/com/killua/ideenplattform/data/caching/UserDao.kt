package com.killua.ideenplattform.data.caching

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.killua.ideenplattform.data.models.local.IdeaCaching
import com.killua.ideenplattform.data.models.local.UserCaching

@Dao
interface UserDao {
    @Query("select * from user_table where userId =:id ")
    fun getUserWithId(id: String):List<UserCaching>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUser(vararg userCaching: UserCaching)

}