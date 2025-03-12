package net.bobacktech.bionicboarder.commsWithVESC

import net.bobacktech.bionicboarder.TestBase
import net.bobacktech.bionicboarder.comms.ClassicBluetoothVescConnector
import net.bobacktech.bionicboarder.comms.VescConnectorFactory
import net.bobacktech.bionicboarder.vesc.Connector
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class ClassicBluetoothConnectionTest : TestBase() {

    private lateinit var connector: Connector

    @BeforeAll
    fun prepare() {
        // TODO: Add setup code if needed
    }

    @Test
    fun `verify ClassicBluetoothVescConnector is constructed properly`() {
        val device = vescBluetoothDevice()
        try {
            connector = VescConnectorFactory.createClassicBluetoothVescConnector(device, 100)
        } catch (e: Exception) {
            fail("Failed to create Classic Bluetooth VESC connector", e)
        }
        assert(connector is ClassicBluetoothVescConnector)
    }

    @AfterAll
    fun tearDown() {
        try {
            connector.shutdown()
        } catch (e: Exception) {
            fail("Failed to shutdown ClassicBluetoothVescConnector", e)
        }
    }
}