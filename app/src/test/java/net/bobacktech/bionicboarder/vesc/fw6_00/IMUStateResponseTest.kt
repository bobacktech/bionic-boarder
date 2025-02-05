package net.bobacktech.bionicboarder.vesc.fw6_00

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class IMUStateResponseTest {

    private lateinit var imuStateResponse: IMUStateReponse

    @BeforeEach
    fun setup() {
        imuStateResponse = IMUStateReponse()
    }

    @Test
    fun `test response ID is correctly set`() {
        assertEquals(65, imuStateResponse.responseID)
    }

    @Test
    fun `test populateImpl correctly parses response packet`() {

        val responsePacket = UByteArray(69)

        val testValues = floatArrayOf(
            1.0f, 2.0f, 3.0f,     // roll, pitch, yaw
            4.0f, 5.0f, 6.0f,     // accelX, accelY, accelZ
            7.0f, 8.0f, 9.0f,     // gyroX, gyroY, gyroZ
            10.0f, 11.0f, 12.0f,  // magX, magY, magZ
            13.0f, 14.0f, 15.0f, 16.0f  // quat1, quat2, quat3, quat4
        )

        // Convert float values to bytes and populate response packet
        var index = 5
        testValues.forEach { value ->
            val bits = value.toBits().toUInt()
            responsePacket[index] = (bits shr 24).toUByte()
            responsePacket[index + 1] = (bits shr 16).toUByte()
            responsePacket[index + 2] = (bits shr 8).toUByte()
            responsePacket[index + 3] = bits.toUByte()
            index += 4
        }

        // Populate the response object
        imuStateResponse.populate(responsePacket)

        // Verify all values are correctly parsed
        assertEquals(1.0f, imuStateResponse.roll)
        assertEquals(2.0f, imuStateResponse.pitch)
        assertEquals(3.0f, imuStateResponse.yaw)
        assertEquals(4.0f, imuStateResponse.accelX)
        assertEquals(5.0f, imuStateResponse.accelY)
        assertEquals(6.0f, imuStateResponse.accelZ)
        assertEquals(7.0f, imuStateResponse.gyroX)
        assertEquals(8.0f, imuStateResponse.gyroY)
        assertEquals(9.0f, imuStateResponse.gyroZ)
        assertEquals(10.0f, imuStateResponse.magX)
        assertEquals(11.0f, imuStateResponse.magY)
        assertEquals(12.0f, imuStateResponse.magZ)
        assertEquals(13.0f, imuStateResponse.quat1)
        assertEquals(14.0f, imuStateResponse.quat2)
        assertEquals(15.0f, imuStateResponse.quat3)
        assertEquals(16.0f, imuStateResponse.quat4)
    }

}