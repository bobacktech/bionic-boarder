package net.bobacktech.bionicboarder.comms

import android.bluetooth.BluetoothSocket
import android.os.SystemClock
import net.bobacktech.bionicboarder.vesc.Connector
import net.bobacktech.bionicboarder.vesc.QueryProducer
import net.bobacktech.bionicboarder.vesc.fw6_00.Query

/**
 * This class represents a classic Bluetooth VESC connector. It is responsible for the communication between the app and the VESC over
 * a classic Bluetooth connection.
 */
class ClassicBluetoothVescConnector(
    private val bluetoothSocket: BluetoothSocket,
    override val responseTimeout_ms: Int
) : Connector() {

    override lateinit var firmwareVersion: FirmwareVersion
        private set

    override lateinit var qp: QueryProducer
        private set

    /**
     * This method uses the FW 6.00 specific query to determine the firmware version of the VESC that the app is connected to. It
     * assumes that the this query format is the same for all firmware versions.
     * Based on the firmware version, it sets the [firmwareVersion] and the [qp] properties.
     *
     * @throws [FirmwareVersionNotSupportedException] if the firmware version is not supported.
     */
    override fun determineFirmwareVersion() {
        val fwq = Query.FirmwareVersion()
        val packet = fwq.form()
        sendBytes(packet)
        val response = readBytes(10)
        val major = response[3].toString()
        val minor = "0" + response[4].toString()
        val key = major + "_$minor"
        try {
            firmwareVersion = FirmwareVersion.valueOf("FW_$key")
            if (firmwareVersion == FirmwareVersion.FW_6_00) {
                this.qp = net.bobacktech.bionicboarder.vesc.fw6_00.QueryProducer()
            } else {
                throw IllegalArgumentException("Unsupported firmware version: $key")
            }
        } catch (e: IllegalArgumentException) {
            throw FirmwareVersionNotSupportedException("Firmware version $key is not supported")
        }
    }

    override fun sendBytes(packet: UByteArray) {
        bluetoothSocket.outputStream.write(packet.toByteArray())
    }

    override fun readBytes(numBytes: Int): UByteArray {
        val data = ByteArray(numBytes)
        var totalBytesRead = 0
        val startTime = SystemClock.elapsedRealtime()
        var elapsedTime: Long
        while (totalBytesRead < numBytes) {
            val bytesAvailable = bluetoothSocket.inputStream.available()
            if (bytesAvailable > 0) bluetoothSocket.inputStream.read(
                data,
                totalBytesRead,
                bytesAvailable
            )
            totalBytesRead += bytesAvailable
            elapsedTime = SystemClock.elapsedRealtime() - startTime
            if (elapsedTime >= responseTimeout_ms) throw ResponseTimeoutException("Response timeout exceeded: $responseTimeout_ms ms")
        }
        return data.toUByteArray()
    }

}