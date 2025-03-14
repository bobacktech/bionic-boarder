package net.bobacktech.bionicboarder.vesc.fw6_00

import kotlin.experimental.and

/**
 *  This class, for Firmware 6.00, represents an abstraction of a command for some information from the VESC
 *  or to command the VESC to take some action. It specifies the [crc16Table] and how to create and format the unsigned
 *  byte array that will be sent to the VESC.
 */
abstract class Command<DATA> : net.bobacktech.bionicboarder.vesc.Command<DATA>() {

    /**
     *  This method formulates the VESC command and returns a unsigned byte array. It must compute the CRC value and add
     *  that to the byte array.
     */
    override fun packetize(data: UByteArray): UByteArray {
        val packet = UByteArray(256)
        var i = 0
        if (data.size <= 256) {
            packet[i++] = 2u
            packet[i++] = data.size.toUByte()
        } else if (data.size <= 65536) {
            packet[i++] = 3u
            packet[i++] = (data.size shr 8).toUByte()
            packet[i++] = (data.size and 0xFF).toUByte()
        } else {
            packet[i++] = 4u
            packet[i++] = (data.size shr 16).toUByte()
            packet[i++] = ((data.size shr 8) and 0xFF).toUByte()
            packet[i++] = (data.size and 0xFF).toUByte()
        }
        data.copyInto(packet, i)

        // Compute CRC value
        i += data.size
        var k = 0
        var cksum = 0
        while (k < data.size) {
            cksum = crc16Table[cksum shr 8 xor data[k++].toInt() and 0xFF].toInt() xor (cksum shl 8)
        }
        val crcValue = cksum.toShort()

        packet[i++] = (crcValue.toInt() shr 8).toUByte()
        packet[i++] = (crcValue and 0x00FF).toUByte()
        packet[i++] = 3u
        packet[i] = 0u
        return packet
    }

    /**
     * CRC table for firmware 6.00.  Used in message byte arrays to/from VESC.
     */
    override val crc16Table = shortArrayOf(
        0x0000.toShort(),
        0x1021.toShort(),
        0x2042.toShort(),
        0x3063.toShort(),
        0x4084.toShort(),
        0x50a5.toShort(),
        0x60c6.toShort(),
        0x70e7.toShort(),
        0x8108.toShort(),
        0x9129.toShort(),
        0xa14a.toShort(),
        0xb16b.toShort(),
        0xc18c.toShort(),
        0xd1ad.toShort(),
        0xe1ce.toShort(),
        0xf1ef.toShort(),
        0x1231.toShort(),
        0x0210.toShort(),
        0x3273.toShort(),
        0x2252.toShort(),
        0x52b5.toShort(),
        0x4294.toShort(),
        0x72f7.toShort(),
        0x62d6.toShort(),
        0x9339.toShort(),
        0x8318.toShort(),
        0xb37b.toShort(),
        0xa35a.toShort(),
        0xd3bd.toShort(),
        0xc39c.toShort(),
        0xf3ff.toShort(),
        0xe3de.toShort(),
        0x2462.toShort(),
        0x3443.toShort(),
        0x0420.toShort(),
        0x1401.toShort(),
        0x64e6.toShort(),
        0x74c7.toShort(),
        0x44a4.toShort(),
        0x5485.toShort(),
        0xa56a.toShort(),
        0xb54b.toShort(),
        0x8528.toShort(),
        0x9509.toShort(),
        0xe5ee.toShort(),
        0xf5cf.toShort(),
        0xc5ac.toShort(),
        0xd58d.toShort(),
        0x3653.toShort(),
        0x2672.toShort(),
        0x1611.toShort(),
        0x0630.toShort(),
        0x76d7.toShort(),
        0x66f6.toShort(),
        0x5695.toShort(),
        0x46b4.toShort(),
        0xb75b.toShort(),
        0xa77a.toShort(),
        0x9719.toShort(),
        0x8738.toShort(),
        0xf7df.toShort(),
        0xe7fe.toShort(),
        0xd79d.toShort(),
        0xc7bc.toShort(),
        0x48c4.toShort(),
        0x58e5.toShort(),
        0x6886.toShort(),
        0x78a7.toShort(),
        0x0840.toShort(),
        0x1861.toShort(),
        0x2802.toShort(),
        0x3823.toShort(),
        0xc9cc.toShort(),
        0xd9ed.toShort(),
        0xe98e.toShort(),
        0xf9af.toShort(),
        0x8948.toShort(),
        0x9969.toShort(),
        0xa90a.toShort(),
        0xb92b.toShort(),
        0x5af5.toShort(),
        0x4ad4.toShort(),
        0x7ab7.toShort(),
        0x6a96.toShort(),
        0x1a71.toShort(),
        0x0a50.toShort(),
        0x3a33.toShort(),
        0x2a12.toShort(),
        0xdbfd.toShort(),
        0xcbdc.toShort(),
        0xfbbf.toShort(),
        0xeb9e.toShort(),
        0x9b79.toShort(),
        0x8b58.toShort(),
        0xbb3b.toShort(),
        0xab1a.toShort(),
        0x6ca6.toShort(),
        0x7c87.toShort(),
        0x4ce4.toShort(),
        0x5cc5.toShort(),
        0x2c22.toShort(),
        0x3c03.toShort(),
        0x0c60.toShort(),
        0x1c41.toShort(),
        0xedae.toShort(),
        0xfd8f.toShort(),
        0xcdec.toShort(),
        0xddcd.toShort(),
        0xad2a.toShort(),
        0xbd0b.toShort(),
        0x8d68.toShort(),
        0x9d49.toShort(),
        0x7e97.toShort(),
        0x6eb6.toShort(),
        0x5ed5.toShort(),
        0x4ef4.toShort(),
        0x3e13.toShort(),
        0x2e32.toShort(),
        0x1e51.toShort(),
        0x0e70.toShort(),
        0xff9f.toShort(),
        0xefbe.toShort(),
        0xdfdd.toShort(),
        0xcffc.toShort(),
        0xbf1b.toShort(),
        0xaf3a.toShort(),
        0x9f59.toShort(),
        0x8f78.toShort(),
        0x9188.toShort(),
        0x81a9.toShort(),
        0xb1ca.toShort(),
        0xa1eb.toShort(),
        0xd10c.toShort(),
        0xc12d.toShort(),
        0xf14e.toShort(),
        0xe16f.toShort(),
        0x1080.toShort(),
        0x00a1.toShort(),
        0x30c2.toShort(),
        0x20e3.toShort(),
        0x5004.toShort(),
        0x4025.toShort(),
        0x7046.toShort(),
        0x6067.toShort(),
        0x83b9.toShort(),
        0x9398.toShort(),
        0xa3fb.toShort(),
        0xb3da.toShort(),
        0xc33d.toShort(),
        0xd31c.toShort(),
        0xe37f.toShort(),
        0xf35e.toShort(),
        0x02b1.toShort(),
        0x1290.toShort(),
        0x22f3.toShort(),
        0x32d2.toShort(),
        0x4235.toShort(),
        0x5214.toShort(),
        0x6277.toShort(),
        0x7256.toShort(),
        0xb5ea.toShort(),
        0xa5cb.toShort(),
        0x95a8.toShort(),
        0x8589.toShort(),
        0xf56e.toShort(),
        0xe54f.toShort(),
        0xd52c.toShort(),
        0xc50d.toShort(),
        0x34e2.toShort(),
        0x24c3.toShort(),
        0x14a0.toShort(),
        0x0481.toShort(),
        0x7466.toShort(),
        0x6447.toShort(),
        0x5424.toShort(),
        0x4405.toShort(),
        0xa7db.toShort(),
        0xb7fa.toShort(),
        0x8799.toShort(),
        0x97b8.toShort(),
        0xe75f.toShort(),
        0xf77e.toShort(),
        0xc71d.toShort(),
        0xd73c.toShort(),
        0x26d3.toShort(),
        0x36f2.toShort(),
        0x0691.toShort(),
        0x16b0.toShort(),
        0x6657.toShort(),
        0x7676.toShort(),
        0x4615.toShort(),
        0x5634.toShort(),
        0xd94c.toShort(),
        0xc96d.toShort(),
        0xf90e.toShort(),
        0xe92f.toShort(),
        0x99c8.toShort(),
        0x89e9.toShort(),
        0xb98a.toShort(),
        0xa9ab.toShort(),
        0x5844.toShort(),
        0x4865.toShort(),
        0x7806.toShort(),
        0x6827.toShort(),
        0x18c0.toShort(),
        0x08e1.toShort(),
        0x3882.toShort(),
        0x28a3.toShort(),
        0xcb7d.toShort(),
        0xdb5c.toShort(),
        0xeb3f.toShort(),
        0xfb1e.toShort(),
        0x8bf9.toShort(),
        0x9bd8.toShort(),
        0xabbb.toShort(),
        0xbb9a.toShort(),
        0x4a75.toShort(),
        0x5a54.toShort(),
        0x6a37.toShort(),
        0x7a16.toShort(),
        0x0af1.toShort(),
        0x1ad0.toShort(),
        0x2ab3.toShort(),
        0x3a92.toShort(),
        0xfd2e.toShort(),
        0xed0f.toShort(),
        0xdd6c.toShort(),
        0xcd4d.toShort(),
        0xbdaa.toShort(),
        0xad8b.toShort(),
        0x9de8.toShort(),
        0x8dc9.toShort(),
        0x7c26.toShort(),
        0x6c07.toShort(),
        0x5c64.toShort(),
        0x4c45.toShort(),
        0x3ca2.toShort(),
        0x2c83.toShort(),
        0x1ce0.toShort(),
        0x0cc1.toShort(),
        0xef1f.toShort(),
        0xff3e.toShort(),
        0xcf5d.toShort(),
        0xdf7c.toShort(),
        0xaf9b.toShort(),
        0xbfba.toShort(),
        0x8fd9.toShort(),
        0x9ff8.toShort(),
        0x6e17.toShort(),
        0x7e36.toShort(),
        0x4e55.toShort(),
        0x5e74.toShort(),
        0x2e93.toShort(),
        0x3eb2.toShort(),
        0x0ed1.toShort(),
        0x1ef0.toShort()
    )


    /**
     *  These are the FW 6.00 specific queries.
     */
    class FirmwareVersion : Command<Nothing>() {
        override val ID: UByte = 0u

        override fun createIDAndDataByteArray(data: Nothing?): UByteArray {
            return ubyteArrayOf(ID)
        }
    }

    class Heartbeat : Command<Nothing>() {
        override val ID: UByte = 30u

        override fun createIDAndDataByteArray(data: Nothing?): UByteArray {
            return ubyteArrayOf(ID)
        }
    }

    class State : Command<Nothing>() {
        override val ID: UByte = 4u

        override fun createIDAndDataByteArray(data: Nothing?): UByteArray {
            return ubyteArrayOf(ID)
        }
    }

    class IMUState : Command<Nothing>() {
        override val ID: UByte = 65u

        override fun createIDAndDataByteArray(data: Nothing?): UByteArray {
            return ubyteArrayOf(ID)
        }
    }

    class Reboot : Command<Nothing>() {
        override val ID: UByte = 29u

        override fun createIDAndDataByteArray(data: Nothing?): UByteArray {
            return ubyteArrayOf(ID)
        }
    }

    class Current : Command<Double>() {
        override val ID: UByte = 6u

        override fun createIDAndDataByteArray(data: Double?): UByteArray {
            val b = UByteArray(5)
            b[0] = ID
            val d = (data!! * 1000.0).toInt()
            b[1] = (d shr 24).toUByte()
            b[2] = (d shr 16).toUByte()
            b[3] = (d shr 8).toUByte()
            b[4] = d.toUByte()
            return b
        }
    }

    class RPM : Command<Int>() {
        override val ID: UByte = 8u

        override fun createIDAndDataByteArray(data: Int?): UByteArray {
            val b = UByteArray(5)
            b[0] = ID
            b[1] = (data!! shr 24).toUByte()
            b[2] = (data shr 16).toUByte()
            b[3] = (data shr 8).toUByte()
            b[4] = data.toUByte()
            return b
        }
    }
}