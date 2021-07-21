package com.killua.ideenplattform.data.repository

import com.killua.ideenplattform.data.models.api.Idea
import com.killua.ideenplattform.data.models.local.CategoryCaching
import kotlinx.coroutines.flow.Flow

interface MainRepository{
    suspend fun getAllCategories(): Flow<Any>
    suspend fun getAllIdeas(): Flow<Any>
}