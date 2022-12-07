package com.auf.cea.openbreweryapiactivity

import android.app.Application
import io.realm.Realm

class BreweryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}