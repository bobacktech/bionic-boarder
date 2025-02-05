package net.bobacktech.bionicboarder.vesc.fw6_00

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*

class StateResponseTest {

    private lateinit var stateResponse: StateResponse

    @BeforeEach
    fun setUp() {
        stateResponse = StateResponse()
    }

    @Test
    fun `test response ID is initialized correctly`() {
        assertEquals(4, stateResponse.responseID)
    }

    @Test
    fun `test populateImpl with valid response packet`() {
        // Create a sample response packet
        // Format: [start_byte, LENGTH_HIGH, LENGTH_LOW, packet_id, ...data...]
        val responsePacket = UByteArray(56) { 0U } // Create array with proper size

        // Set some test values in the packet
        // Mosfet temp (index 3-4): 35.5°C * 10
        responsePacket[3] = 0x01.toUByte()
        responsePacket[4] = 0x63.toUByte()

        // Motor temp (index 5-6): 40.2°C * 10
        responsePacket[5] = 0x01.toUByte()
        responsePacket[6] = 0x92.toUByte()

        // Motor current (index 7-10): 15.5A * 100
        responsePacket[7] = 0x00u
        responsePacket[8] = 0x00u
        responsePacket[9] = 0x06U
        responsePacket[10] = 0x0Eu

        // Duty cycle (index 23-24): 0.75 * 1000
        responsePacket[23] = 0x02u
        responsePacket[24] = 0xEEu

        // RPM (index 25-28): 2500
        responsePacket[25] = 0x00u
        responsePacket[26] = 0x00u
        responsePacket[27] = 0x09u
        responsePacket[28] = 0xC4u

        // Input voltage (index 29-30): 36.5V * 10
        responsePacket[29] = 0x01u
        responsePacket[30] = 0x6Du

        // Watt hours (index 39-42): 100.5Wh * 10000
        responsePacket[39] = 0x00u
        responsePacket[40] = 0x0Fu
        responsePacket[41] = 0x55u
        responsePacket[42] = 0xC8u

        // Fault code (index 55): FAULT_CODE_DRV
        responsePacket[55] = 3U

        // Populate the state response with the test packet
        stateResponse.populate(responsePacket)

        // Verify all values are parsed correctly
        assertEquals(35.5f, stateResponse.mosfetTemp, 0.01f)
        assertEquals(40.2f, stateResponse.motorTemp, 0.01f)
        assertEquals(15.5f, stateResponse.motorCurrent, 0.01f)
        assertEquals(2500, stateResponse.rpm)
        assertEquals(0.75f, stateResponse.dutyCycle, 0.01f)
        assertEquals(100.5f, stateResponse.wattHours, 0.01f)
        assertEquals(36.5f, stateResponse.inputVoltage, 0.01f)
        assertEquals("FAULT_CODE_DRV", stateResponse.fault)
    }

    @Test
    fun `test fault code parsing`() {
        val responsePacket = UByteArray(56) { 0U }

        // Test different fault codes
        val testCases = mapOf(
            5U to "FAULT_CODE_OVER_TEMP_FET"
        )

        testCases.forEach { (faultCode, expectedFault) ->
            responsePacket[55] = faultCode.toUByte()
            stateResponse.populate(responsePacket)
            assertEquals(expectedFault, stateResponse.fault)
        }
    }

}