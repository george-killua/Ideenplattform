package com.killua.ideenplattform.data.repository

import com.killua.ideenplattform.data.caching.IdeasDao
import com.killua.ideenplattform.data.caching.UserDao
import com.killua.ideenplattform.data.models.api.Idea
import com.killua.ideenplattform.data.network.ApiServices

class MainRepositoryImpl(val api: ApiServices, val userDao: UserDao, val ideasDao: IdeasDao):
    MainRepository {
   override fun getAllIdeas(): MutableList<Idea>{
return  mutableListOf()
    }
}