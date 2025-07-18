package net.bobacktech.bionicboarder.csf


/**
 * Abstract class for spike detection algorithms that operate on IMU data from both VESC and Smartphone.
 * This class provides a framework for detecting spikes in the IMU data streams and recording the results.
 *
 * @property vescImuBuffer The buffer containing VESC IMU data.
 * @property smartphoneImuBuffer The buffer containing Smartphone IMU data.
 */
abstract class SpikeDetectionIMUAlgo(
    protected val vescImuBuffer: VescImuBuffer,
    protected val smartphoneImuBuffer: SmartphoneImuBuffer
) {

    /**
     * A description of the specific detection algorithm, which should be concise and informative.
     */
    abstract var description: String


    /**
     * Records the detected spikes in the VESC IMU data.
     * Each entry is a pair of the maximum long-axis acceleration from the VESC IMU and the timestamp when this occurred.
     */
    protected var _vescImuSpikeRecord: ArrayList<Pair<Double, Long>> = arrayListOf()
    val vescImuSpikeRecord: List<Pair<Double, Long>>
        get() = _vescImuSpikeRecord.toList()

    /**
     * Records the detected spikes in the Smartphone IMU data.
     * Each entry is a pair of the magnitude of the acceleration vector from the Smartphone IMU and the timestamp when this occurred.
     */
    protected var _smartphoneImuSpikeRecord: ArrayList<Pair<Double, Long>> = arrayListOf()
    val smartphoneImuSpikeRecord: List<Pair<Double, Long>>
        get() = _smartphoneImuSpikeRecord.toList()

    /**
     * Detects a spike in the interval of the IMU data set that is defined by the start and end timestamps.
     * This method will use the internal buffers to find the relevant data points and apply the spike detection algorithm.
     *
     * @param intervalStartTime_ms The start time of the candidate spike in milliseconds.
     * @param intervalEndTime_ms The end time of the candidate spike in milliseconds.
     * @return True if a spike is detected, false otherwise.
     */
    fun detectSpike(intervalStartTime_ms: Long, intervalEndTime_ms: Long): Boolean {
        val vescImuBufferInitialElementIteratorPair =
            vescImuBuffer.findVESCIteratorAtOrBeforeTimestamp(intervalEndTime_ms)
        val smartphoneImuBufferInitialElementIteratorPair =
            smartphoneImuBuffer.findSmartphoneIteratorAtOrBeforeTimestamp(intervalEndTime_ms)

        return executeSpikeDetectAlgo(
            vescImuBufferInitialElementIteratorPair!!,
            smartphoneImuBufferInitialElementIteratorPair!!,
            intervalStartTime_ms
        )
    }

    /**
     * Executes the spike detection algorithm using the provided initial elements and iterators
     * for both VESC IMU and Smartphone IMU data streams.
     *
     * The initial element represents the first data point whose timestamp is less than or equal
     * to the interval end time. The algorithm analyzes data from this starting point backward
     * through the timeline until reaching a data element with a timestamp less than or equal
     * to the interval start time, determining whether a spike occurs within the specified interval.
     *
     * This method is abstract and must be implemented by subclasses to define the specific
     * spike detection logic.
     *
     * @param vescImuBufferInitialElementIteratorPair A pair containing the initial element of the VESC IMU buffer and its iterator.
     * @param smartphoneImuBufferInitialElementIteratorPair A pair containing the initial element of the Smartphone IMU buffer and its iterator.
     * @param intervalStartTime_ms The start time of the candidate spike in milliseconds.
     * @return True if a spike is detected, false otherwise.
     */
    protected abstract fun executeSpikeDetectAlgo(
        vescImuBufferInitialElementIteratorPair: Pair<VescImuStateTimed, Iterator<VescImuStateTimed>>,
        smartphoneImuBufferInitialElementIteratorPair: Pair<SmartphoneImuTimed, Iterator<SmartphoneImuTimed>>,
        candidateStartTime_ms: Long
    ): Boolean

    /**
     * Resets the internal state of the spike detection algorithm.
     * This clears the recorded spikes for both VESC IMU and Smartphone IMU, and calls the internal reset method.
     */
    fun reset() {
        _vescImuSpikeRecord.clear()
        _smartphoneImuSpikeRecord.clear()
        _resetInternal()
    }

    /**
     * Internal reset method to be implemented by subclasses for any additional reset logic.
     * This is called when the `reset()` method is invoked.
     */
    protected abstract fun _resetInternal()
}
