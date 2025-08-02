package net.bobacktech.bionicboarder.csf

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import net.bobacktech.bionicboarder.utils.MissionClock
import net.bobacktech.bionicboarder.vesc.Connector
import net.bobacktech.bionicboarder.vesc.fw6_00.CommandProducer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


@OptIn(ExperimentalCoroutinesApi::class)
class VESCAndIMUDataCollectorTest {

    private class TestConnector : Connector() {
        override val responseTimeout_ms: Int
            get() = 1000

        override val firmwareVersion: FirmwareVersion
            get() = FirmwareVersion.FW_6_00

        override val qp: CommandProducer = CommandProducer()


        override fun determineFirmwareVersion() {
            TODO("Shouldn't be called in test")
        }

        override fun sendBytes(packet: UByteArray) {
            if (genException) {
                // Simulate an exception
                throw Exception("Simulated exception")
            }
        }

        override fun readBytes(numBytes: Int): UByteArray {
            if (timeout) {
                // Simulate a timeout
                throw ResponseTimeoutException("Simulated timeout")
            }
            return UByteArray(80)
        }

        override fun shutdown() {
            TODO("Shouldn't be called in test")
        }

        private var timeout = false
        fun activateTimeout() {
            // Simulate a timeout
            timeout = true
        }

        private var genException = false
        fun activateException() {
            // Simulate a timeout
            genException = true
        }
    }

    private lateinit var testConnector: TestConnector
    private lateinit var mockMissionClock: MissionClock
    private lateinit var vescBuffer: VescStateBuffer
    private lateinit var imuBuffer: VescImuBuffer
    private lateinit var dataCollector: VESCAndIMUDataCollector
    private lateinit var testDispatcher: TestDispatcher

    @BeforeEach
    fun setup() {
        testConnector = TestConnector()
        mockMissionClock = mockk()
        vescBuffer = VescStateBuffer()
        imuBuffer = VescImuBuffer()
        testDispatcher = StandardTestDispatcher()

        dataCollector = VESCAndIMUDataCollector(
            testConnector,
            mockMissionClock,
            vescBuffer,
            imuBuffer
        )
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `collectData should collect and store data successfully`() = runTest(testDispatcher) {
        val timestamp = 12345L
        every { mockMissionClock() } returns timestamp

        // Act
        val job = launch {
            dataCollector.collectData()
        }

        advanceTimeBy(10) // Allow some execution
        job.cancel()

        // Assert
        assertTrue(vescBuffer.isNotEmpty())
        assertTrue(imuBuffer.isNotEmpty())

        assertEquals(timestamp, vescBuffer.first.timestamp_ms)
        assertEquals(timestamp, imuBuffer.first.timestamp_ms)
    }

    @Test
    fun `collectData should handle request timeout properly`() = runTest(testDispatcher) {
        val timestamp = 12345L
        every { mockMissionClock() } returns timestamp

        testConnector.activateTimeout()

        // Act
        val job = launch {
            dataCollector.collectData()
        }

        advanceTimeBy(10) // Allow some execution
        job.cancel()

        // Assert
        assertTrue(vescBuffer.isEmpty())
        assertTrue(imuBuffer.isEmpty())
    }

    @Test
    fun `collectData should break on general Exception`() = runTest(testDispatcher) {
        val timestamp = 12345L
        every { mockMissionClock() } returns timestamp

        testConnector.activateException()

        // Act
        val job = launch {
            dataCollector.collectData()
        }

        advanceTimeBy(10) // Allow some execution

        // Assert
        assertTrue(vescBuffer.isEmpty())
        assertTrue(imuBuffer.isEmpty())
        assertTrue(job.isCompleted) // Job should complete due to break
    }
}