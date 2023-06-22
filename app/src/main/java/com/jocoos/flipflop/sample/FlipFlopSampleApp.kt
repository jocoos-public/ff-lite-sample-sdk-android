package com.jocoos.flipflop.sample

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.jocoos.flipflop.FFLServer
import com.jocoos.flipflop.FlipFlopLite
import com.jocoos.flipflop.sample.utils.PreferenceManager

class FlipFlopSampleApp : Application() {
    companion object {
        lateinit var preferenceManager: PreferenceManager
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        preferenceManager = PreferenceManager(applicationContext)

        // initialize FlipFlop Lite
        // there are two servers to connect : DEV, PROD
        FlipFlopLite.initialize(server = FFLServer.DEV)
    }
}
