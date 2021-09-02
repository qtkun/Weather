package com.qtk.weather.application

import android.app.Application
import com.qtk.weather.koin.appModule
import com.qtk.weather.koin.viewModelModule
import com.qtk.weather.utils.DelegatesExt
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App: Application() {
    companion object {
        var instance: App by DelegatesExt.notNullSingleValue()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin {
            androidLogger()
            androidContext(this@App)
            androidFileProperties()
            modules(listOf(viewModelModule))
            modules(listOf(appModule))
        }
    }
}