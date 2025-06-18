package net.bobacktech.bionicboarder.utils

import android.os.SystemClock


/**
 * This class is used to track the elapsed time of a Rider's mission.
 */
class MissionClock {

    private var startTime: Long = 0

    /**
     * Sets the start time to the current elapsed real time.
     */
    fun start() {
        startTime = SystemClock.elapsedRealtime()
    }

    /**
     * Computes the elapsed time since the mission started.
     */
    operator fun invoke(): Long {
        return (SystemClock.elapsedRealtime() - startTime)
    }
}