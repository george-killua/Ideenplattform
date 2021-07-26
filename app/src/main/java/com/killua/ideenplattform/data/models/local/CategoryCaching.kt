package com.killua.ideenplattform.data.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryCaching(
    @PrimaryKey(autoGenerate = false)
    val id: String = "",
    //example: sport-and-fun
    val name_en: String = "",
    //  example: Sport & Fun
    val name_de: String = ""
    //example: Sport & Spaß
)