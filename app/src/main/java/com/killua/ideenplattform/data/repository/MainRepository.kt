package com.killua.ideenplattform.data.repository

import com.killua.ideenplattform.data.models.api.Idea

interface MainRepository{
    fun getAllIdeas(): MutableList<Idea>
}