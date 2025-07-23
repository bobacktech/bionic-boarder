package net.bobacktech.bionicboarder.csf

abstract class PaddleDetectionStrategy {

    protected lateinit var spikeDetectionIMUAlgo: SpikeDetectionIMUAlgo

    fun setSpikeDetectionIMUAlgo(spikeDetectionIMUAlgo: SpikeDetectionIMUAlgo) {
        this.spikeDetectionIMUAlgo = spikeDetectionIMUAlgo
    }

    protected val computedVelocityCurve: ArrayList<Pair<Int, Long>> = arrayListOf()

    fun getComputedVelocityCurve(): List<Pair<Int, Long>> {
        if (computedVelocityCurve.isNotEmpty()) {
            val r = computedVelocityCurve.toList()
            reset()
            return r
        } else {
            return emptyList()
        }
    }

    private var initialPaddleContactEndTime_ms: Long = 0
    private var lastErpmDataPointInitialPaddleContactWindow: Pair<Int, Long>? = null

    protected val erpmBuffer: ArrayList<Pair<Int, Long>> = arrayListOf()

    fun addErpmDataPoint(erpm: Int, timestamp: Long) {
        erpmBuffer.add(Pair(erpm, timestamp))
        if (erpmBuffer.size > 1) {
            if (timestamp <= initialPaddleContactEndTime_ms) {
                if (erpmBuffer[erpmBuffer.size - 2].first <= erpm) {
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
                            spikeDetectionIMUAlgo.detectSpike(
                                startEndTimes.first,
                                startEndTimes.second
                            )
                        if (spikeDetected) {
                            computePaddleVelocityCurve()
                        }
                    }
                }
            }
        }
        if (erpmBuffer.size == 1) {
            initialPaddleContactEndTime_ms = timestamp + computeInitialPaddleContactTimeDuration()
        }
    }

    fun reset() {
        erpmBuffer.clear()
        computedVelocityCurve.clear()
        initialPaddleContactEndTime_ms = 0
        lastErpmDataPointInitialPaddleContactWindow = null
        resetImpl()
    }

    protected abstract fun resetImpl()

    protected abstract fun doesPaddleSpeedDataPointSatisfyDetectionStrategy(erpm: Int, timestamp: Long): Boolean

    protected abstract fun computeInitialPaddleContactTimeDuration(): Long

    protected abstract fun computePaddleVelocityCurve()

    protected abstract fun determinePaddleStartAndEndTimes(): Pair<Long, Long>
}