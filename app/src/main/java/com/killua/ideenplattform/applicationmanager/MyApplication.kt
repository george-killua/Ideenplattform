package com.killua.ideenplattform.applicationmanager

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.ashokvarma.gander.Gander
import com.ashokvarma.gander.persistence.GanderPersistence
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin




class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(databaseModule, httpModule, apiModule, repoModule,moduleBuilder)
        }
        instance = this
        Gander.setGanderStorage(GanderPersistence.getInstance(this));


    }
    fun isOnline():Boolean{
        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
       return cm.isActiveNetworkMetered
    }

    companion object {
        lateinit var instance: MyApplication
            private set
    }
}