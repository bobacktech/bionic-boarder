package net.bobacktech.bionicboarder.utils

import android.os.SystemClock
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MissionClockTest {

    private lateinit var missionClock: MissionClock

    @BeforeEach
    fun setUp() {
        missionClock = MissionClock()
        mockkStatic(SystemClock::class)
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(SystemClock::class)
    }

    @Test
    fun `start should set start time to current elapsed realtime`() {
        // Given
        val mockStartTime = 1000L
        every { SystemClock.elapsedRealtime() } returns mockStartTime

        // When
        missionClock.start()

        // Then
        // We can verify the start time was set by checking elapsed time
        every { SystemClock.elapsedRealtime() } returns mockStartTime
        assertEquals(0L, missionClock())
    }

    @Test
    fun `invoke should return elapsed time since start`() {
        // Given
        val startTime = 1000L
        val currentTime = 5000L
        val expectedElapsed = currentTime - startTime

        every { SystemClock.elapsedRealtime() } returns startTime
        missionClock.start()

        every { SystemClock.elapsedRealtime() } returns currentTime

        // When
        val elapsedTime = missionClock()

        // Then
        assertEquals(expectedElapsed, elapsedTime)
    }

    @Test
    fun `invoke should return zero when called immediately after start`() {
        // Given
        val mockTime = 2000L
        every { SystemClock.elapsedRealtime() } returns mockTime

        // When
        missionClock.start()
        val elapsedTime = missionClock()

        // Then
        assertEquals(0L, elapsedTime)
    }

    @Test
    fun `invoke should return correct elapsed time for multiple calls`() {
        // Given
        val startTime = 1000L
        val firstCallTime = 3000L
        val secondCallTime = 7000L

        every { SystemClock.elapsedRealtime() } returns startTime
        missionClock.start()

        // When & Then - First call
        every { SystemClock.elapsedRealtime() } returns firstCallTime
        assertEquals(2000L, missionClock())

        // When & Then - Second call
        every { SystemClock.elapsedRealtime() } returns secondCallTime
        assertEquals(6000L, missionClock())
    }

    @Test
    fun `invoke should return negative start time when called before start`() {
        // Given
        val currentTime = 5000L
        every { SystemClock.elapsedRealtime() } returns currentTime

        // When
        val elapsedTime = missionClock()

        // Then
        // Since startTime defaults to 0, this should return the current time
        assertEquals(currentTime, elapsedTime)
    }

    @Test
    fun `start can be called multiple times to reset the clock`() {
        // Given
        val firstStartTime = 1000L
        val secondStartTime = 5000L
        val finalTime = 8000L

        // First start
        every { SystemClock.elapsedRealtime() } returns firstStartTime
        missionClock.start()

        // Second start (reset)
        every { SystemClock.elapsedRealtime() } returns secondStartTime
        missionClock.start()

        // Final measurement
        every { SystemClock.elapsedRealtime() } returns finalTime

        // When
        val elapsedTime = missionClock()

        // Then
        assertEquals(finalTime - secondStartTime, elapsedTime)
    }
}