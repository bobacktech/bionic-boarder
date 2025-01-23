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
    @DisplayName("packetize method adds the data value in the proper segment of the packet")
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

    @Test
    @DisplayName("verify Firmware Version query ID is in the packet")
    fun testFirmwareVersionQuery() {
        val fwq = Query.FirmwareVersion()
        val packet = fwq.form()
        assert(packet[2] == fwq.queryID)
    }

    @Test
    @DisplayName("verify Heartbeat query ID is in the packet")
    fun testHeartbeatQuery() {
        val hbq = Query.Heartbeat()
        val packet = hbq.form()
        assert(packet[2] == hbq.queryID)
    }

    @Test
    @DisplayName("verify State query ID is in the packet")
    fun testStateQuery() {
        val sq = Query.State()
        val packet = sq.form()
        assert(packet[2] == sq.queryID)
    }

    @Test
    @DisplayName("verify IMU State query ID is in the packet")
    fun testIMUStateQuery() {
        val imuq = Query.IMUState()
        val packet = imuq.form()
        assert(packet[2] == imuq.queryID)
    }

    @Test
    @DisplayName("verify Reboot query ID is in the packet")
    fun testRebootQuery() {
        val rq = Query.Reboot()
        val packet = rq.form()
        assert(packet[2] == rq.queryID)
    }

    @Test
    @DisplayName("verify RPM query ID and data value are in the packet")
    fun testRPMQuery() {
        val rpmq = Query.RPM()
        val rpm = 1234
        val packet = rpmq.form(rpm)
        assert(packet[2] == rpmq.queryID)
        val uBytes = packet.sliceArray(3..6)
        val r = (uBytes[0].toInt() shl 24) or
                (uBytes[1].toInt() shl 16) or
                (uBytes[2].toInt() shl 8) or
                uBytes[3].toInt()
        assertThat(r).isEqualTo(rpm)
    }

    @Test
    @DisplayName("verify Current query ID and data value are in the packet")
    fun testCurrentQuery() {
        val currentq = Query.Current()
        val current = 23.5
        val packet = currentq.form(current)
        assert(packet[2] == currentq.queryID)
        val uBytes = packet.sliceArray(3..6)
        val r = ((uBytes[0].toInt() shl 24) or
                (uBytes[1].toInt() shl 16) or
                (uBytes[2].toInt() shl 8) or
                uBytes[3].toInt()) / 1000.0
        assertThat(r).isEqualTo(current)
    }
}