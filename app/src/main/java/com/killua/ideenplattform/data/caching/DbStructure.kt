package com.killua.ideenplattform.data.caching

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.killua.ideenplattform.data.models.local.IdeaCaching
import com.killua.ideenplattform.data.models.local.UserCaching

@Database(
    entities = [UserCaching::class, IdeaCaching::class], version = 1,
    exportSchema = false
)
abstract class DbStructure:RoomDatabase() {
    abstract val userDao: UserDao
    abstract val ideaDao: IdeasDao
}
