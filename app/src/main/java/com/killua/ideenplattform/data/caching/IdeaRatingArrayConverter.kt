package com.killua.ideenplattform.data.caching

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.killua.ideenplattform.data.models.local.IdeaRatingCaching
import java.lang.reflect.Type

class IdeaRatingArrayConverter{

        @TypeConverter
        fun stringToWeathers(json : String?) : Array<IdeaRatingCaching> {
            val gson = Gson()
            val type : Type = object : TypeToken<Array<IdeaRatingCaching?>?>() {}.type
            return gson.fromJson(json, type)
        }

        @TypeConverter
        fun weathersToString(array : Array<IdeaRatingCaching?>?) : String {
            val gson = Gson()
            val type : Type = object : TypeToken<Array<IdeaRatingCaching?>?>() {}.type
            return gson.toJson(array, type)
        }

}