package com.killua.ideenplattform.data.caching

import androidx.room.*
import com.killua.ideenplattform.data.models.local.IdeaCaching

@Dao
interface IdeaDao {
    @Query("select * from idea_table order by created desc")
    fun getAllIdeas():List<IdeaCaching>?

    @Query("select * from idea_table where ideaCachingId =:id order by created desc")
    fun getIdeaWithId(id: String):IdeaCaching?
    @Query("SELECT * FROM idea_table WHERE description Like '%' || :searchText || '%' OR title Like '%' || :searchText || '%' ORDER BY title DESC ")
    fun getCityBySearchText(searchText:String):List<IdeaCaching>?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addIdeas(vararg ideaCaching: IdeaCaching)

    @Delete
    fun remove(ideaCaching: IdeaCaching)
}

