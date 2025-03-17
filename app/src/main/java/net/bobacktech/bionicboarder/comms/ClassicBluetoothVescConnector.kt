package net.bobacktech.bionicboarder.comms

import android.bluetooth.BluetoothSocket
import android.os.SystemClock
import net.bobacktech.bionicboarder.vesc.CommandProducer
import net.bobacktech.bionicboarder.vesc.Connector
import net.bobacktech.bionicboarder.vesc.fw6_00.Command

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

    override lateinit var qp: CommandProducer
        private set

    /**
     * This method uses the FW 6.00 specific command to determine the firmware version of the VESC that the app is connected to. It
     * assumes that the this command format is the same for all firmware versions.
     * Based on the firmware version, it sets the [firmwareVersion] and the [qp] properties.
     *
     * @throws [FirmwareVersionNotSupportedException] if the firmware version is not supported.
     */
    override fun determineFirmwareVersion() {
        val fwq = Command.FirmwareVersion()
        val packet = fwq.form()
        sendBytes(packet)
        val response = readBytes(10)
        val major = response[3].toString()
        val minor = "0" + response[4].toString()
        val key = major + "_$minor"
        try {
            firmwareVersion = FirmwareVersion.valueOf("FW_$key")
            if (firmwareVersion == FirmwareVersion.FW_6_00) {
                this.qp = net.bobacktech.bionicboarder.vesc.fw6_00.CommandProducer()
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
            val remainingSpace = numBytes - totalBytesRead
            val bytesToRead = minOf(bytesAvailable, remainingSpace)
            if (bytesAvailable > 0) {
                bluetoothSocket.inputStream.read(
                    data,
                    totalBytesRead,
                    bytesToRead
                )
                totalBytesRead += bytesToRead
            }
            elapsedTime = SystemClock.elapsedRealtime() - startTime
            if (elapsedTime >= responseTimeout_ms) throw ResponseTimeoutException("Response timeout exceeded: $responseTimeout_ms ms")
        }

        // Discard the remaining bytes in the input stream to clear the stream in preparation for the next response.
        val discardBuffer = ByteArray(1024)
        while (bluetoothSocket.inputStream.available() > 0) {
            bluetoothSocket.inputStream.read(discardBuffer)
            Thread.sleep(10)
        }
        return data.toUByteArray()
    }

    override fun shutdown() {
        try {
            bluetoothSocket.apply {
                outputStream.flush()
                outputStream.close()
                inputStream.close()
                close()
            }
        } catch (e: Exception) {
            // TODO: Log error
        }
    }
}