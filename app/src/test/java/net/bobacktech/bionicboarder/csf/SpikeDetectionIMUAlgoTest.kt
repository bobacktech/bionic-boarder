package net.bobacktech.bionicboarder.csf

import io.mockk.mockk
import net.bobacktech.bionicboarder.vesc.IMUStateResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SpikeDetectionIMUAlgoTest {

    private lateinit var vescImuBuffer: VescImuBuffer
    private lateinit var smartphoneImuBuffer: SmartphoneImuBuffer
    private lateinit var spikeDetectionAlgo: TestSpikeDetectionIMUAlgo

    private class TestSpikeDetectionIMUAlgo(
        vescImuBuffer: VescImuBuffer,
        smartphoneImuBuffer: SmartphoneImuBuffer
    ) : SpikeDetectionIMUAlgo(vescImuBuffer, smartphoneImuBuffer) {

        override var description: String = "Test Algorithm"
        override fun executeSpikeDetectAlgo(
            vescImuBufferInitialElementIteratorPair: Pair<VescImuStateTimed, Iterator<VescImuStateTimed>>,
            smartphoneImuBufferInitialElementIteratorPair: Pair<SmartphoneImuTimed, Iterator<SmartphoneImuTimed>>,
            intervalStartTime_ms: Long
        ): Boolean {
            return true
        }

        override fun _resetInternal() {
            // No-op for testing
        }
    }

    @BeforeEach
    fun setUp() {
        vescImuBuffer = VescImuBuffer((1..10).map { i ->
            VescImuStateTimed(mockk<IMUStateResponse>(), 2 * i.toLong())
        })
        smartphoneImuBuffer = SmartphoneImuBuffer((1..10).map { i ->
            SmartphoneImuTimed(99.999, 2 * i.toLong())
        })
        spikeDetectionAlgo = TestSpikeDetectionIMUAlgo(vescImuBuffer, smartphoneImuBuffer)
    }

    @Test
    fun `verify that logic in detectSpike method executes with no error`() {
        val intervalStartTime_ms = 3L
        val intervalEndTime_ms = 8L
        assert(spikeDetectionAlgo.detectSpike(intervalStartTime_ms, intervalEndTime_ms))
    }
}