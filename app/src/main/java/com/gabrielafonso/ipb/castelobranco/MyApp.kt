package com.gabrielafonso.ipb.castelobranco

import android.app.Application
import com.gabrielafonso.ipb.castelobranco.core.di.AppContainer

class MyApp : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}