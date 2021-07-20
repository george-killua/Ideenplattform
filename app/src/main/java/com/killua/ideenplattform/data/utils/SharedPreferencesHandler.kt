package com.killua.ideenplattform.data.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.killua.ideenplattform.R
import com.killua.ideenplattform.data.models.api.User
import java.lang.reflect.Type

class SharedPreferencesHandler(val context: Context) {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(
            context.getString(R.string.GJERPIGNJpsijpt34welkfms),
            Context.MODE_PRIVATE
        )
    }

    val userLoader =
        stringToUser(
            prefs.getString(
                context.getString(R.string.GJERPIGNJpsijpt34welkejngfsgfms), ""
            )
        )

    fun saveUserContent(user: User) = prefs.edit().putString(
        context.getString(R.string.GJERPIGNJpsijpt34welkejngfsgfms), userToString(user)
    ).apply()

    companion object {
        private val gson = Gson()
        private val type: Type = object : TypeToken<User?>() {}.type
        fun stringToUser(json: String?): User? {
            return gson.fromJson(json, type)
        }

        fun userToString(user: User?): String {
            return gson.toJson(user, type)
        }
    }
}