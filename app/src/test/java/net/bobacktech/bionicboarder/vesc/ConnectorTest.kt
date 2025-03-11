package net.bobacktech.bionicboarder.vesc

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.bobacktech.bionicboarder.vesc.fw6_00.FirmwareVersionResponse
import net.bobacktech.bionicboarder.vesc.fw6_00.IMUStateReponse
import net.bobacktech.bionicboarder.vesc.fw6_00.StateResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ConnectorTest {

    private lateinit var testConnector: TestConnector
    private lateinit var mockCommandProducer: CommandProducer

    @BeforeEach
    fun setup() {
        mockCommandProducer = mockk()
        testConnector = TestConnector(mockCommandProducer)
    }

    @Test
    fun `test firmware version string conversion`() {
        assertEquals("6.00", Connector.FirmwareVersion.FW_6_00.toString())
    }

    @Test
    fun `test requestResponse with FirmwareVersionResponse`() {
        // Arrange
        val commandChoice = CommandProducer.CommandChoice.FW_VERSION
        val testPacket = ubyteArrayOf(1u, 2u, 3u)
        val testResponse = UByteArray(65)

        every { mockCommandProducer.invoke(commandChoice) } returns testPacket
        testConnector.setMockResponse(testResponse)

        // Act
        val response: FirmwareVersionResponse = testConnector.requestResponse(commandChoice)

        // Assert
        assertNotNull(response)
        verify { mockCommandProducer.invoke(commandChoice) }
    }

    @Test
    fun `test requestResponse with StateResponse`() {
        // Arrange
        val commandChoice = CommandProducer.CommandChoice.STATE
        val testPacket = ubyteArrayOf(1u, 2u, 3u)
        val testResponse = UByteArray(73)

        every { mockCommandProducer.invoke(commandChoice) } returns testPacket
        testConnector.setMockResponse(testResponse)

        // Act
        val response: StateResponse = testConnector.requestResponse(commandChoice)

        // Assert
        assertNotNull(response)
        verify { mockCommandProducer.invoke(commandChoice) }
    }

    @Test
    fun `test requestResponse with IMUStateResponse`() {
        // Arrange
        val commandChoice = CommandProducer.CommandChoice.IMU_STATE
        val testPacket = ubyteArrayOf(1u, 2u, 3u)
        val testResponse = UByteArray(70)

        every { mockCommandProducer.invoke(commandChoice) } returns testPacket
        testConnector.setMockResponse(testResponse)

        // Act
        val response: IMUStateReponse = testConnector.requestResponse(commandChoice)

        // Assert
        assertNotNull(response)
        verify { mockCommandProducer.invoke(commandChoice) }
    }

    @Test
    fun `test sendCommand without data`() {
        // Arrange
        val commandChoice = CommandProducer.CommandChoice.HEARTBEAT
        val testPacket = ubyteArrayOf(1u, 2u, 3u)

        every { mockCommandProducer.invoke(commandChoice) } returns testPacket

        // Act
        testConnector.sendCommand(commandChoice)

        // Assert
        verify { mockCommandProducer.invoke(commandChoice) }
    }

    @Test
    fun `test sendCommand with data`() {
        // Arrange
        val commandChoice = CommandProducer.CommandChoice.RPM
        val testData = 42
        val testPacket = ubyteArrayOf(1u, 2u, 3u)

        every { mockCommandProducer.invoke(commandChoice, testData) } returns testPacket

        // Act
        testConnector.sendCommand(commandChoice, testData)

        // Assert
        verify { mockCommandProducer.invoke(commandChoice, testData) }
    }

    private class TestConnector(commandProducer: CommandProducer) : Connector() {

        override val responseTimeout_ms: Int
            get() = TODO("Not yet implemented")
        override var firmwareVersion: FirmwareVersion = FirmwareVersion.FW_6_00
        override val qp: CommandProducer = commandProducer

        private var mockResponse: UByteArray = ubyteArrayOf()
        var wasCommandSent: Boolean = false
        var lastSentCommand: UByteArray? = null

        fun setMockResponse(response: UByteArray) {
            mockResponse = response
        }

        override fun determineFirmwareVersion() {
            // Implementation for testing
        }

        override fun sendBytes(packet: UByteArray) {
            wasCommandSent = true
            lastSentCommand = packet
        }

        override fun readBytes(numBytes: Int): UByteArray {
            return mockResponse
        }

        override fun shutdown() {
            TODO("Not yet implemented")
        }
    }
}