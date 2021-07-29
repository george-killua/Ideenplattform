package com.killua.ideenplattform.data.caching

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomWarnings
import androidx.room.TypeConverters
import com.killua.ideenplattform.data.models.local.CategoryCaching
import com.killua.ideenplattform.data.models.local.IdeaCaching
import com.killua.ideenplattform.data.models.local.UserCaching

@Database(
    entities = [UserCaching::class, IdeaCaching::class, CategoryCaching::class], version = 1,
    exportSchema = false
)
@TypeConverters(IdeaRatingArrayConverter::class)
abstract class DbStructure : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val ideaDao: IdeaDao
    abstract val categoryDao: CategoryDao
}
