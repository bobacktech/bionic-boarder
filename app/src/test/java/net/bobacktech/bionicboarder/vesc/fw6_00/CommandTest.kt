package net.bobacktech.bionicboarder.vesc.fw6_00

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class CommandTest {
    private class TestCommand : Command<Int>() {
        override val ID: UByte = 1u
        override fun createIDAndDataByteArray(data: Int?): UByteArray =
            UByteArray(5) { i ->
                when (i) {
                    0 -> ID
                    else -> (data!! shr (32 - 8 * i)).toUByte()
                }
            }
    }

    private var command: TestCommand = TestCommand()

    @Test
    @DisplayName("packetize method adds the data value in the proper segment of the packet")
    fun testPacketizeWithData() {
        val data: Int = 1234
        val packet: UByteArray = command.form(data)
        val uBytes = packet.sliceArray(3..6)
        val r = (uBytes[0].toInt() shl 24) or
                (uBytes[1].toInt() shl 16) or
                (uBytes[2].toInt() shl 8) or
                uBytes[3].toInt()
        assertThat(r).isEqualTo(data)
    }

    @Test
    @DisplayName("verify Firmware Version command ID is in the packet")
    fun testFirmwareVersionCommand() {
        val fwq = Command.FirmwareVersion()
        val packet = fwq.form()
        assert(packet[2] == fwq.ID)
    }

    @Test
    @DisplayName("verify Heartbeat command ID is in the packet")
    fun testHeartbeatCommand() {
        val hbq = Command.Heartbeat()
        val packet = hbq.form()
        assert(packet[2] == hbq.ID)
    }

    @Test
    @DisplayName("verify State command ID is in the packet")
    fun testStateCommand() {
        val sq = Command.State()
        val packet = sq.form()
        assert(packet[2] == sq.ID)
    }

    @Test
    @DisplayName("verify IMU State command ID is in the packet")
    fun testIMUStateCommand() {
        val imuq = Command.IMUState()
        val packet = imuq.form()
        assert(packet[2] == imuq.ID)
    }

    @Test
    @DisplayName("verify Reboot command ID is in the packet")
    fun testRebootCommand() {
        val rq = Command.Reboot()
        val packet = rq.form()
        assert(packet[2] == rq.ID)
    }

    @Test
    @DisplayName("verify RPM command ID and data value are in the packet")
    fun testRPMCommand() {
        val rpmq = Command.RPM()
        val rpm = 1234
        val packet = rpmq.form(rpm)
        assert(packet[2] == rpmq.ID)
        val uBytes = packet.sliceArray(3..6)
        val r = (uBytes[0].toInt() shl 24) or
                (uBytes[1].toInt() shl 16) or
                (uBytes[2].toInt() shl 8) or
                uBytes[3].toInt()
        assertThat(r).isEqualTo(rpm)
    }

    @Test
    @DisplayName("verify Current command ID and data value are in the packet")
    fun testCurrentCommand() {
        val currentq = Command.Current()
        val current = 23.5
        val packet = currentq.form(current)
        assert(packet[2] == currentq.ID)
        val uBytes = packet.sliceArray(3..6)
        val r = ((uBytes[0].toInt() shl 24) or
                (uBytes[1].toInt() shl 16) or
                (uBytes[2].toInt() shl 8) or
                uBytes[3].toInt()) / 1000.0
        assertThat(r).isEqualTo(current)
    }
}