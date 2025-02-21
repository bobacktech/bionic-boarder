package net.bobacktech.bionicboarder.comms

import android.bluetooth.BluetoothSocket
import android.os.SystemClock
import net.bobacktech.bionicboarder.vesc.Connector
import net.bobacktech.bionicboarder.vesc.QueryProducer

class ClassicBluetoothVescConnector(
    private val bluetoothSocket: BluetoothSocket,
    private val RESPONSE_TIMEOUT_MS: Int
) : Connector() {

    override lateinit var firmwareVersion: FirmwareVersion
        private set

    override lateinit var qp: QueryProducer
        private set


    override fun determineFirmwareVersion() {
        TODO("Not yet implemented")
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
            if (elapsedTime >= RESPONSE_TIMEOUT_MS) throw MessageReadTimeoutException()
        }
        if (totalBytesRead > numBytes) throw MessageSizeLessThanBytesReadException()
        return data.toUByteArray()
    }

}