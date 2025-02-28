package net.bobacktech.bionicboarder.comms

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import net.bobacktech.bionicboarder.vesc.Connector
import java.util.UUID

@SuppressLint("MissingPermission")
object VescConnectorFactory {

    /**
     * Creates a classic Bluetooth VESC connector for the given device.
     *
     * @param device The Bluetooth device to connect to.
     * @param responseTimeoutMs The timeout in milliseconds for the response.
     * @return Connector The classic Bluetooth VESC connector.
     */
    fun createClassicBluetoothVescConnector(device: BluetoothDevice, responseTimeoutMs: Int): Connector {
        val uuidSpp: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        val bs = device.createInsecureRfcommSocketToServiceRecord(uuidSpp)
        val classicBluetoothVescConnector = ClassicBluetoothVescConnector(bs, 50)
        return classicBluetoothVescConnector
    }

    /**
     * Creates a BLE VESC connector.
     *
     * @return Connector The BLE VESC connector.
     */
    fun createBLEVescConnector(): Connector {
        throw NotImplementedError("BLE VESC connector not implemented")
    }

    /**
     * Creates a WiFi VESC connector.
     *
     * @return Connector The WiFi VESC connector.
     */
    fun createWifiVescConnector(): Connector {
        throw NotImplementedError("WiFi VESC connector not implemented")
    }

}