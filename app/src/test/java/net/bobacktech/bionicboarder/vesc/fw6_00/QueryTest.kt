package net.bobacktech.bionicboarder.vesc.fw6_00

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class QueryTest {
    private class TestQuery : Query<Int>() {
        override val queryID: UByte = 1u
        override fun createIDAndDataByteArray(data: Int?): UByteArray =
            UByteArray(5) { i ->
                when (i) {
                    0 -> queryID
                    else -> (data!! shr (32 - 8 * i)).toUByte()
                }
            }
    }

    private var query: TestQuery = TestQuery()

    @Test
    @DisplayName("packetize() adds the data value in the proper segment of the packet")
    fun testPacketizeWithData() {
        val data: Int = 1234
        val packet: UByteArray = query.form(data)
        val uBytes = packet.sliceArray(3..6)
        val r = (uBytes[0].toInt() shl 24) or
                (uBytes[1].toInt() shl 16) or
                (uBytes[2].toInt() shl 8) or
                uBytes[3].toInt()
        assertThat(r).isEqualTo(data)
    }
}