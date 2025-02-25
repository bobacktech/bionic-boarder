package net.bobacktech.bionicboarder.vesc

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class QueryTest {

    // Concrete implementation for testing with Int data
    private class TestQuery : Query<Int>() {
        override val queryID: UByte = 0x1u

        override fun createIDAndDataByteArray(data: Int?): UByteArray {
            return if (data == null) {
                ubyteArrayOf(queryID)
            } else {
                ubyteArrayOf(queryID) + data.toByteArray()
            }
        }

        override fun packetize(data: UByteArray): UByteArray {
            // Simple implementation for testing
            return ubyteArrayOf(0xFEu) + data + calculateCRC()
        }

        override val crc16Table: ShortArray = ShortArray(256) { 0 }

        private fun calculateCRC(): UByteArray {
            // Dummy CRC calculation for testing
            return ubyteArrayOf(0xAAu, 0xBBu)
        }

        // Helper function to convert Int to UByteArray
        private fun Int.toByteArray(): UByteArray {
            return ubyteArrayOf(
                (this shr 24 and 0xFF).toUByte(),
                (this shr 16 and 0xFF).toUByte(),
                (this shr 8 and 0xFF).toUByte(),
                (this and 0xFF).toUByte()
            )
        }
    }

    private lateinit var query: TestQuery


    @BeforeEach
    fun setup() {
        query = TestQuery()
    }

    @Nested
    @DisplayName("Query Formation Tests")
    inner class QueryFormationTests {

        @Test
        @DisplayName("form() should create correct packet structure without data")
        fun formShouldCreateCorrectPacketWithoutData() {
            val result = query.form()
            assertThat(result.size).isGreaterThanOrEqualTo(4) // Header + ID + CRC (minimum)
            assertThat(result[0]).isEqualTo(0xFEu.toUByte()) // Header
            assertThat(result[1]).isEqualTo(0x1u.toUByte())  // Query ID
            assertTrue(result.takeLast(2).toUByteArray().contentEquals(ubyteArrayOf(0xAAu, 0xBBu))) // CRC
        }

        @Test
        @DisplayName("form() should create correct packet structure with data")
        fun formShouldCreateCorrectPacketWithData() {
            val testData = 12345
            val result = query.form(testData)

            assertThat(result.size).isGreaterThan(4) // Header + ID + Data + CRC
            assertThat(result[0]).isEqualTo(0xFEu.toUByte()) // Header
            assertThat(result[1]).isEqualTo(0x1u.toUByte())  // Query ID
            val intBytes = result.slice(2..5).toUByteArray()
            val exp: Int = intBytes[0].toInt() shl 24 or
                    (intBytes[1].toInt() shl 16) or
                    (intBytes[2].toInt() shl 8) or
                    intBytes[3].toInt()
            assertThat(exp).isEqualTo(testData) // Data
            assertTrue(result.takeLast(2).toUByteArray().contentEquals(ubyteArrayOf(0xAAu, 0xBBu))) // CRC
        }
    }


    @Nested
    @DisplayName("Query Properties Tests")
    inner class QueryPropertiesTests {
        @Test
        @DisplayName("queryID should return correct value")
        fun queryIDShouldReturnCorrectValue() {
            assertThat(query.queryID).isEqualTo(0x1u.toUByte())
        }
    }
}