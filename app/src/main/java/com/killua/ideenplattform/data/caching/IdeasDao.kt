package com.killua.ideenplattform.data.caching

import androidx.room.*
import com.killua.ideenplattform.data.models.local.IdeaCaching
import com.killua.ideenplattform.data.models.local.UserCaching
import kotlinx.coroutines.flow.Flow

@Dao
interface IdeasDao {
    @Query("select * from idea_table order by created desc")
    fun getAllIdeas()

    @Query("select * from idea_table where id =:id order by created desc")
    fun getIdeaWithId(id: String):List<IdeaCaching>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addIdeas(vararg ideaCaching: IdeaCaching)

    @Delete
    fun remove(ideaCaching: IdeaCaching)
}

@Dao
interface UserDao {
    @Query("select * from user_table where id =:id ")
    fun getUserWithId(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUser(vararg userCaching: UserCaching)

}