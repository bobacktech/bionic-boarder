package net.bobacktech.bionicboarder.vesc.fw6_00

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*

class FirmwareVersionResponseTest {

    private lateinit var firmwareVersionResponse: FirmwareVersionResponse

    @BeforeEach
    fun setUp() {
        firmwareVersionResponse = FirmwareVersionResponse()
    }

    @Test
    fun `test populateImpl with valid response packet`() {
        // Arrange
        // Creating a sample response packet
        // Format: [3 bytes header][major][minor][hwName][NULL TERMINATOR][15 bytes padding][hwType]
        val hwName = "TEST_HW"
        val responsePacket = ubyteArrayOf(
            0x00U, 0x00U, 0x00U,  // 3 bytes header
            0x02U,                 // major version
            0x05U,                 // minor version
            *hwName.toByteArray().map { it.toUByte() }.toUByteArray(),  // hwName
            0u,  // null terminator
            *UByteArray(15) { 0U },   // 15 bytes padding
            0x01U                     // hwType
        )

        // Act
        firmwareVersionResponse.populate(responsePacket)

        // Assert
        assertEquals("2", firmwareVersionResponse.versionMajor)
        assertEquals("5", firmwareVersionResponse.versionMinor)
        val expectedDescription = """
            Firmware Version: 2.5
            HW Name: TEST_HW
            HW Type: 1
        """.trimIndent()
        assertEquals(expectedDescription, firmwareVersionResponse.versionDescription)
    }

    @Test
    fun `test responseID initialization`() {
        assertEquals(0, firmwareVersionResponse.responseID)
    }

}
