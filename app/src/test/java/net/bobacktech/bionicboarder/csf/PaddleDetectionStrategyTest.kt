package net.bobacktech.bionicboarder.csf

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PaddleDetectionStrategyTest {

    private lateinit var paddleDetectionStrategy: TestPaddleDetectionStrategy
    private lateinit var mockSpikeDetectionIMUAlgo: SpikeDetectionIMUAlgo

    @BeforeEach
    fun setUp() {
        mockSpikeDetectionIMUAlgo = mockk()
        paddleDetectionStrategy = TestPaddleDetectionStrategy()
        paddleDetectionStrategy.setSpikeDetectionIMUAlgo(mockSpikeDetectionIMUAlgo)
    }

    @Test
    fun `verify getComputedVelocityCurve returns empty list when no data computed`() {
        // When
        val result = paddleDetectionStrategy.getComputedVelocityCurve()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `verify getComputedVelocityCurve returns computed data and resets after retrieval`() {
        // Given
        val testData = listOf(Pair(100, 1000L), Pair(200, 2000L))
        paddleDetectionStrategy.setComputedVelocityCurve(testData)

        // When
        val result = paddleDetectionStrategy.getComputedVelocityCurve()
        val secondResult = paddleDetectionStrategy.getComputedVelocityCurve()

        // Then
        assertEquals(testData, result)
        assertTrue(secondResult.isEmpty())
    }

    @Test
    fun `verify addErpmDataPoint sets initial paddle contact end time on first data point`() {
        // Given
        val erpm = 100
        val timestamp = 1000L
        val expectedDuration = 500L
        paddleDetectionStrategy.setInitialPaddleContactTimeDuration(expectedDuration)

        // When
        paddleDetectionStrategy.addErpmDataPoint(erpm, timestamp)

        // Then
        assertEquals(
            timestamp + expectedDuration,
            paddleDetectionStrategy.initialPaddleContactEndTime_ms
        )
        assertEquals(1, paddleDetectionStrategy.getErpmBufferSize())
    }

    @Test
    fun `verify addErpmDataPoint calls reset when within initial contact window and erpm increasing`() {
        // Given
        paddleDetectionStrategy.setInitialPaddleContactTimeDuration(1000L)
        paddleDetectionStrategy.addErpmDataPoint(100, 1000L) // First point

        // When
        paddleDetectionStrategy.addErpmDataPoint(150, 1500L) // Within window, increasing

        // Then
        assertEquals(paddleDetectionStrategy.resetImplCalled, 1) // Reset occurred
    }

    @Test
    fun `verify addErpmDataPoint does not call reset when within initial contact window and erpm decreasing`() {
        // Given
        paddleDetectionStrategy.setInitialPaddleContactTimeDuration(1000L)
        paddleDetectionStrategy.addErpmDataPoint(150, 1000L) // First point

        // When
        paddleDetectionStrategy.addErpmDataPoint(100, 1500L) // Within window, decreasing

        // Then
        assertEquals(2, paddleDetectionStrategy.getErpmBufferSize()) // Reset occurred
        assertEquals(paddleDetectionStrategy.resetImplCalled, 0)
    }

    @Test
    fun `verify addErpmDataPoint sets last erpm data point when exiting initial contact window`() {
        // Given
        paddleDetectionStrategy.setInitialPaddleContactTimeDuration(500L)
        paddleDetectionStrategy.addErpmDataPoint(100, 1000L) // First point
        paddleDetectionStrategy.addErpmDataPoint(75, 1200L) // Second point, within window

        // When
        paddleDetectionStrategy.addErpmDataPoint(200, 2000L) // Third point, outside window

        // Then
        assertEquals(
            Pair(75, 1200L),
            paddleDetectionStrategy.lastErpmDataPointInitialPaddleContactWindow
        )
    }

    @Test
    fun `verify addErpmDataPoint calls detection strategy when outside initial window and erpm increasing`() {
        // Given
        paddleDetectionStrategy.setInitialPaddleContactTimeDuration(500L)
        paddleDetectionStrategy.setSatisfiesDetectionStrategy(true)
        paddleDetectionStrategy.addErpmDataPoint(100, 1000L)
        paddleDetectionStrategy.addErpmDataPoint(75, 1200L)

        // When
        paddleDetectionStrategy.addErpmDataPoint(200, 2000L)

        // Then
        assertTrue(paddleDetectionStrategy.doesPaddleSpeedDataPointSatisfyDetectionStrategyCalled)
    }

    @Test
    fun `verify addErpmDataPoint calls reset when detection strategy not satisfied`() {
        // Given
        paddleDetectionStrategy.setInitialPaddleContactTimeDuration(500L)
        paddleDetectionStrategy.setSatisfiesDetectionStrategy(false)
        paddleDetectionStrategy.addErpmDataPoint(100, 1000L)
        paddleDetectionStrategy.addErpmDataPoint(75, 1200L)

        // When
        paddleDetectionStrategy.addErpmDataPoint(200, 2000L)

        // Then
        assertEquals(paddleDetectionStrategy.resetImplCalled, 1) // Reset occurred
    }

    @Test
    fun `verify addErpmDataPoint calls reset when erpm drops below last initial contact window value`() {
        // Given
        paddleDetectionStrategy.setInitialPaddleContactTimeDuration(500L)
        paddleDetectionStrategy.addErpmDataPoint(200, 1000L)
        paddleDetectionStrategy.addErpmDataPoint(250, 1200L)
        paddleDetectionStrategy.addErpmDataPoint(300, 2000L) // Sets last initial contact window

        // When
        paddleDetectionStrategy.addErpmDataPoint(150, 2500L) // Below 250

        // Then
        assertEquals(paddleDetectionStrategy.resetImplCalled, 1) // Reset occurred
    }

    @Test
    fun `verify addErpmDataPoint triggers spike detection when erpm decreases after initial window`() {
        // Given
        paddleDetectionStrategy.setInitialPaddleContactTimeDuration(500L)
        paddleDetectionStrategy.setPaddleStartAndEndTimes(Pair(1000L, 3000L))
        every { mockSpikeDetectionIMUAlgo.detectSpike(1000L, 3000L) } returns true

        paddleDetectionStrategy.addErpmDataPoint(200, 1000L)
        paddleDetectionStrategy.addErpmDataPoint(150, 1200L)
        paddleDetectionStrategy.addErpmDataPoint(300, 2000L) // Sets last initial contact window

        // When
        paddleDetectionStrategy.addErpmDataPoint(280, 2500L) // Decreasing but above threshold

        // Then
        verify(exactly = 1) { mockSpikeDetectionIMUAlgo.detectSpike(1000L, 3000L) }
    }

    @Test
    fun `verify addErpmDataPoint computes velocity curve when spike detected`() {
        // Given
        paddleDetectionStrategy.setInitialPaddleContactTimeDuration(500L)
        paddleDetectionStrategy.setPaddleStartAndEndTimes(Pair(1000L, 3000L))
        every { mockSpikeDetectionIMUAlgo.detectSpike(1000L, 3000L) } returns true

        paddleDetectionStrategy.addErpmDataPoint(200, 1000L)
        paddleDetectionStrategy.addErpmDataPoint(150, 1200L)
        paddleDetectionStrategy.addErpmDataPoint(300, 2000L)

        // When
        paddleDetectionStrategy.addErpmDataPoint(280, 2500L)

        // Then
        assertTrue(paddleDetectionStrategy.computePaddleVelocityCurveCalled)
    }

    @Test
    fun `verify addErpmDataPoint does not compute velocity curve when spike not detected`() {
        // Given
        paddleDetectionStrategy.setInitialPaddleContactTimeDuration(500L)
        paddleDetectionStrategy.setPaddleStartAndEndTimes(Pair(1000L, 3000L))
        every { mockSpikeDetectionIMUAlgo.detectSpike(1000L, 3000L) } returns false

        paddleDetectionStrategy.addErpmDataPoint(200, 1000L)
        paddleDetectionStrategy.addErpmDataPoint(150, 1200L)
        paddleDetectionStrategy.addErpmDataPoint(300, 2000L)

        // When
        paddleDetectionStrategy.addErpmDataPoint(280, 2500L)

        // Then
        assertFalse(paddleDetectionStrategy.computePaddleVelocityCurveCalled)
    }


    // Test implementation of the abstract class for testing purposes
    private class TestPaddleDetectionStrategy : PaddleDetectionStrategy() {

        var resetImplCalled = 0
        var doesPaddleSpeedDataPointSatisfyDetectionStrategyCalled = false
        var computePaddleVelocityCurveCalled = false
        var determinePaddleStartAndEndTimesCalled = false

        private var initialPaddleContactTimeDuration = 1000L
        private var satisfiesDetectionStrategy = true
        private var paddleStartAndEndTimes = Pair(0L, 0L)

        fun setInitialPaddleContactTimeDuration(duration: Long) {
            this.initialPaddleContactTimeDuration = duration
        }

        fun setSatisfiesDetectionStrategy(satisfies: Boolean) {
            this.satisfiesDetectionStrategy = satisfies
        }

        fun setPaddleStartAndEndTimes(times: Pair<Long, Long>) {
            this.paddleStartAndEndTimes = times
        }

        fun getErpmBufferSize() = erpmBuffer.size

        fun setComputedVelocityCurve(data: List<Pair<Int, Long>>) {
            computedVelocityCurve.clear()
            computedVelocityCurve.addAll(data)
        }

        override fun resetImpl() {
            resetImplCalled++
        }

        override fun doesPaddleSpeedDataPointSatisfyDetectionStrategy(
            erpm: Int,
            timestamp: Long
        ): Boolean {
            doesPaddleSpeedDataPointSatisfyDetectionStrategyCalled = true
            return satisfiesDetectionStrategy
        }

        override fun computeInitialPaddleContactTimeDuration(): Long {
            return initialPaddleContactTimeDuration
        }

        override fun computePaddleVelocityCurve() {
            computePaddleVelocityCurveCalled = true
        }

        override fun determinePaddleStartAndEndTimes(): Pair<Long, Long> {
            determinePaddleStartAndEndTimesCalled = true
            return paddleStartAndEndTimes
        }
    }
}