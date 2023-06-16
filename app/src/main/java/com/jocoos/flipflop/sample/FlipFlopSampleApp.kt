package com.jocoos.flipflop.sample

import android.app.Application
import com.jocoos.flipflop.FFLServer
import com.jocoos.flipflop.FlipFlopLite

class FlipFlopSampleApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // initialize FlipFlop Lite
        // there are two servers to connect : DEV, PROD
        FlipFlopLite.initialize(server = FFLServer.DEV)
    }
}
