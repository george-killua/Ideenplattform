package com.killua.ideenplattform.di

import android.app.Application
import com.ashokvarma.gander.Gander
import com.ashokvarma.gander.persistence.GanderPersistence
import com.jakewharton.threetenabp.AndroidThreeTen
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
        Gander.setGanderStorage(GanderPersistence.getInstance(this))
        AndroidThreeTen.init(this);


    }

    fun isOnline():Boolean{
        val command = "ping -c 1 google.com"
        return Runtime.getRuntime().exec(command).waitFor() == 0
    }

    companion object {
        lateinit var instance: MyApplication
            private set
    }
}