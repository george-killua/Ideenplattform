package com.killua.ideenplattform.data.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.killua.ideenplattform.R
import com.killua.ideenplattform.data.models.local.SharedPrefsUser
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

    fun saveUserContent(user: SharedPrefsUser) = prefs.edit().putString(
        context.getString(R.string.GJERPIGNJpsijpt34welkejngfsgfms), userToString(user)
    ).apply()

    companion object {
        private val gson = Gson()
        private val type: Type = object : TypeToken<SharedPrefsUser?>() {}.type
        fun stringToUser(json: String?): SharedPrefsUser? {
            return gson.fromJson(json, type)
        }

        fun userToString(user: SharedPrefsUser?): String {
            return gson.toJson(user, type)
        }
    }
}