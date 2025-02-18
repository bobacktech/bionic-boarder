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
    private lateinit var mockQueryProducer: QueryProducer

    @BeforeEach
    fun setup() {
        mockQueryProducer = mockk()
        testConnector = TestConnector(mockQueryProducer)
    }

    @Test
    fun `test firmware version string conversion`() {
        assertEquals("6.00", Connector.FirmwareVersion.FW_6_00.toString())
    }

    @Test
    fun `test requestResponse with FirmwareVersionResponse`() {
        // Arrange
        val queryChoice = QueryProducer.QueryChoice.FW_VERSION
        val testPacket = ubyteArrayOf(1u, 2u, 3u)
        val testResponse = UByteArray(65)

        every { mockQueryProducer.invoke(queryChoice) } returns testPacket
        testConnector.setMockResponse(testResponse)

        // Act
        val response: FirmwareVersionResponse = testConnector.requestResponse(queryChoice)

        // Assert
        assertNotNull(response)
        verify { mockQueryProducer.invoke(queryChoice) }
    }

    @Test
    fun `test requestResponse with StateResponse`() {
        // Arrange
        val queryChoice = QueryProducer.QueryChoice.STATE
        val testPacket = ubyteArrayOf(1u, 2u, 3u)
        val testResponse = UByteArray(73)

        every { mockQueryProducer.invoke(queryChoice) } returns testPacket
        testConnector.setMockResponse(testResponse)

        // Act
        val response: StateResponse = testConnector.requestResponse(queryChoice)

        // Assert
        assertNotNull(response)
        verify { mockQueryProducer.invoke(queryChoice) }
    }

    @Test
    fun `test requestResponse with IMUStateResponse`() {
        // Arrange
        val queryChoice = QueryProducer.QueryChoice.IMU_STATE
        val testPacket = ubyteArrayOf(1u, 2u, 3u)
        val testResponse = UByteArray(70)

        every { mockQueryProducer.invoke(queryChoice) } returns testPacket
        testConnector.setMockResponse(testResponse)

        // Act
        val response: IMUStateReponse = testConnector.requestResponse(queryChoice)

        // Assert
        assertNotNull(response)
        verify { mockQueryProducer.invoke(queryChoice) }
    }

    @Test
    fun `test sendQuery without data`() {
        // Arrange
        val queryChoice = QueryProducer.QueryChoice.HEARTBEAT
        val testPacket = ubyteArrayOf(1u, 2u, 3u)

        every { mockQueryProducer.invoke(queryChoice) } returns testPacket

        // Act
        testConnector.sendQuery(queryChoice)

        // Assert
        verify { mockQueryProducer.invoke(queryChoice) }
    }

    @Test
    fun `test sendQuery with data`() {
        // Arrange
        val queryChoice = QueryProducer.QueryChoice.RPM
        val testData = 42
        val testPacket = ubyteArrayOf(1u, 2u, 3u)

        every { mockQueryProducer.invoke(queryChoice, testData) } returns testPacket

        // Act
        testConnector.sendQuery(queryChoice, testData)

        // Assert
        verify { mockQueryProducer.invoke(queryChoice, testData) }
    }

    private class TestConnector(queryProducer: QueryProducer) : Connector() {
        override var firmwareVersion: FirmwareVersion = FirmwareVersion.FW_6_00
        override val qp: QueryProducer = queryProducer

        private var mockResponse: UByteArray = ubyteArrayOf()
        var wasQuerySent: Boolean = false
        var lastSentQuery: UByteArray? = null

        fun setMockResponse(response: UByteArray) {
            mockResponse = response
        }

        override fun determineFirmwareVersion() {
            // Implementation for testing
        }

        override fun sendBytes(packet: UByteArray) {
            wasQuerySent = true
            lastSentQuery = packet
        }

        override fun readBytes(numBytes: Int): UByteArray {
            return mockResponse
        }
    }
}