package net.bobacktech.bionicboarder

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.os.ParcelFileDescriptor
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.fail
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Properties


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class TestBase {

    private lateinit var properties: Properties
    private lateinit var bluetoothManager: BluetoothManager


    @BeforeAll
    fun setup() {
        val testContext = InstrumentationRegistry.getInstrumentation().context
        properties = Properties()
        val assetManager = testContext.assets
        val inputStream = assetManager.open("test.properties")
        properties.load(inputStream)
        inputStream.close()
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val result = instrumentation.uiAutomation.executeShellCommand(
            "pm grant ${instrumentation.targetContext.packageName} ${Manifest.permission.BLUETOOTH_CONNECT}"
        ).use { descriptor ->
            BufferedReader(InputStreamReader(ParcelFileDescriptor.AutoCloseInputStream(descriptor))).use { reader ->
                reader.readText()
            }
        }
        if (result.isNotEmpty()) {
            throw RuntimeException("Shell command failed: $result")
        }
        bluetoothManager = targetContext.getSystemService(BluetoothManager::class.java)
    }

    protected fun vescBluetoothDevice(): BluetoothDevice {
        val deviceName = properties.getProperty("classicBluetoothDeviceName")
        val devices: Set<BluetoothDevice>
        try {
            devices = bluetoothManager.adapter.bondedDevices
            return devices.first { it.name == deviceName }
        } catch (e: Exception) {
            fail("No device with name $deviceName paired with android device", e)
        }
    }

    @AfterAll
    fun cleanup() {
        // TODO: Add cleanup code if needed
    }
}