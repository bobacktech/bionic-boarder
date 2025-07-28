package net.bobacktech.bionicboarder.csf

/**
 * This class defines an abstract strategy for detecting that Rider is paddling in real-time.
 * Any strategy can use any SpikeDetectionIMUAlgo implementation to detect spikes in the IMU data.
 *
 * A detection strategy is responsible for computing the paddle velocity curve based on the VESC
 * ERPM data during the time the Rider is paddling if the spike detection algorithm detects a spike
 * in the IMU data.
 */
abstract class PaddleDetectionStrategy {

    /**
     * The SpikeDetectionIMUAlgo implementation used by this strategy to detect spikes in the IMU data.
     * It is set by the [setSpikeDetectionIMUAlgo] method.
     */
    private lateinit var sda: SpikeDetectionIMUAlgo

    fun setSpikeDetectionIMUAlgo(spikeDetectionIMUAlgo: SpikeDetectionIMUAlgo) {
        this.sda = spikeDetectionIMUAlgo
    }

    /**
     * This is the computed paddle velocity curve.
     * It is a list of pairs where each pair contains the paddle speed in ERPM and a timestamp in milliseconds
     * that represents the mission clock time when the paddle speed was computed.
     */
    protected val computedVelocityCurve: ArrayList<Pair<Int, Long>> = arrayListOf()

    /**
     * Returns the computed paddle velocity curve and resets the internal state if it is not empty.
     * If the computed velocity curve is empty, it returns an empty list to indicate that no velocity
     * curve has been computed yet.
     */
    fun getComputedVelocityCurve(): List<Pair<Int, Long>> {
        if (computedVelocityCurve.isNotEmpty()) {
            val r = computedVelocityCurve.toList()
            reset()
            return r
        } else {
            return emptyList()
        }
    }

    /**
     * There is a time duration right after the the paddle makes contact with the land where this contact
     * actually slows the down the velocity of the longboard. The time duration is defined as from the first data point that
     * the paddle touches the land until [initialPaddleContactEndTime_ms].
     *
     * This data member holds the end time of the initial paddle contact in milliseconds.
     */
    var initialPaddleContactEndTime_ms: Long = 0
        private set

    /**
     * This method computes the initial paddle contact time duration in milliseconds.
     * It is called when the first ERPM data point is added to the buffer.
     */
    protected abstract fun computeInitialPaddleContactTimeDuration(): Long

    /**
     * This data member holds the last ERPM data point that was added to the buffer during the initial paddle contact window.
     * It is used to determine if the next ERPM data point satisfies the detection strategy.
     */
    var lastErpmDataPointInitialPaddleContactWindow: Pair<Int, Long>? = null
        private set

    /**
     * This is the buffer that holds the ERPM data points and their timestamps.
     */
    protected val erpmBuffer: ArrayList<Pair<Int, Long>> = arrayListOf()

    /**
     * Adds a new ERPM data point to the buffer and processes it according to the general paddle detection strategy.
     */
    fun addErpmDataPoint(erpm: Int, timestamp: Long) {
        erpmBuffer.add(Pair(erpm, timestamp))
        if (erpmBuffer.size > 1) {
            if (timestamp <= initialPaddleContactEndTime_ms) {
                if (erpmBuffer[erpmBuffer.size - 2].first >= erpm) {
                    return
                } else {
                    reset()
                }
            } else {
                if (lastErpmDataPointInitialPaddleContactWindow == null) {
                    lastErpmDataPointInitialPaddleContactWindow = erpmBuffer[erpmBuffer.size - 2]
                }
                if (erpmBuffer[erpmBuffer.size - 2].first <= erpm) {
                    if (doesPaddleSpeedDataPointSatisfyDetectionStrategy(erpm, timestamp)) {
                        return
                    } else {
                        reset()
                    }
                } else {
                    if (erpm < lastErpmDataPointInitialPaddleContactWindow!!.first) {
                        reset()
                    } else {
                        val startEndTimes = determinePaddleStartAndEndTimes()
                        val spikeDetected =
                            sda.detectSpike(
                                startEndTimes.first,
                                startEndTimes.second
                            )
                        if (spikeDetected) {
                            computePaddleVelocityCurve()
                        } else {
                            reset()
                        }
                    }
                }
            }
        }
        if (erpmBuffer.size == 1) {
            initialPaddleContactEndTime_ms = timestamp + computeInitialPaddleContactTimeDuration()
        }
    }

    /**
     * Resets the internal state of the paddle detection strategy.
     */
    fun reset() {
        erpmBuffer.clear()
        computedVelocityCurve.clear()
        initialPaddleContactEndTime_ms = 0
        lastErpmDataPointInitialPaddleContactWindow = null
        resetImpl()
    }

    /**
     * This method is called to reset the internal state of the specific paddle detection strategy.
     */
    protected abstract fun resetImpl()

    /**
     * This method checks if the given ERPM data point satisfies the detection strategy.
     * It is called when a new ERPM data point is added to the buffer.
     *
     * @param erpm The ERPM value of the data point.
     * @param timestamp The timestamp of the data point in milliseconds.
     * @return true if the data point satisfies the detection strategy, false otherwise.
     */
    protected abstract fun doesPaddleSpeedDataPointSatisfyDetectionStrategy(erpm: Int, timestamp: Long): Boolean

    /**
     * This method computes the paddle velocity curve based on the ERPM data points in the [erpmBuffer].
     * It is called when a spike is detected in the IMU data.
     * The computed velocity curve is stored in the [computedVelocityCurve] data member.
     */
    protected abstract fun computePaddleVelocityCurve()

    /**
     * This method determines the start and end times for which to analyze the IMU data for spike detection.
     *
     * @return A pair of timestamps representing the start and end times in milliseconds.
     */
    protected abstract fun determinePaddleStartAndEndTimes(): Pair<Long, Long>
}