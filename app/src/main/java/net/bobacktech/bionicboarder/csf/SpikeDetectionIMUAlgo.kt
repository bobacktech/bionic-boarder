package net.bobacktech.bionicboarder.csf


abstract class SpikeDetectionIMUAlgo(
    protected val vescImuBuffer: VescImuBuffer,
    protected val smartphoneImuBuffer: SmartphoneImuBuffer
) {
    abstract var description: String

    protected var _vescImuSpikeRecord: ArrayList<Pair<Double, Long>> = arrayListOf()
    val vescImuSpikeRecord: List<Pair<Double, Long>>
        get() = _vescImuSpikeRecord.toList()

    protected var _smartphoneImuSpikeRecord: ArrayList<Pair<Double, Long>> = arrayListOf()
    val smartphoneImuSpikeRecord: List<Pair<Double, Long>>
        get() = _smartphoneImuSpikeRecord.toList()

    fun detectSpike(candidateStartTime_ms: Long, candidateEndTime_ms: Long): Boolean {
        val vescImuBufferIterator =
            vescImuBuffer.findIteratorAtOrNextOneAfterTimestamp(candidateEndTime_ms)
        val smartphoneImuBufferIterator =
            smartphoneImuBuffer.findIteratorAtOrNextOneAfterTimestamp(candidateEndTime_ms)
        return executeSpikeDetectAlgo(
            vescImuBufferIterator!!,
            smartphoneImuBufferIterator!!,
            candidateStartTime_ms
        )
    }

    protected abstract fun executeSpikeDetectAlgo(
        vescImuBufferIterator: Iterator<VescImuStateTimed>,
        smartphoneImuBufferIterator: Iterator<SmartphoneImuTimed>,
        candidateStartTime_ms: Long
    ): Boolean

    fun reset() {
        _vescImuSpikeRecord.clear()
        _smartphoneImuSpikeRecord.clear()
        _resetInternal()
    }

    protected abstract fun _resetInternal()
}
