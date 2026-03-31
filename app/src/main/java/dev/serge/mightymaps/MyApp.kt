package dev.serge.mightymaps

import android.app.Application
import com.google.android.libraries.places.api.Places

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()

        @Suppress("DEPRECATION")
        Places.initialize(
            applicationContext,
            BuildConfig.MAPS_API_KEY
        )
    }
}