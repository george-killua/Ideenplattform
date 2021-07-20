package com.killua.ideenplattform.data.caching

import androidx.room.*
import com.killua.ideenplattform.data.models.local.IdeaCaching

@Dao
interface IdeasDao {
    @Query("select * from idea_table order by created desc")
    fun getAllIdeas():List<IdeaCaching>

    @Query("select * from idea_table where ideaCachingId =:id order by created desc")
    fun getIdeaWithId(id: String):List<IdeaCaching>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addIdeas(vararg ideaCaching: IdeaCaching)

    @Delete
    fun remove(ideaCaching: IdeaCaching)
}

