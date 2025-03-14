package net.bobacktech.bionicboarder.commsWithVESC

import net.bobacktech.bionicboarder.TestBase
import net.bobacktech.bionicboarder.comms.ClassicBluetoothVescConnector
import net.bobacktech.bionicboarder.comms.VescConnectorFactory
import net.bobacktech.bionicboarder.vesc.CommandProducer
import net.bobacktech.bionicboarder.vesc.Connector
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.fail

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ClassicBluetoothConnectionTest : TestBase() {

    private lateinit var connector: Connector

    @BeforeAll
    fun prepare() {
        // TODO: Add setup code if needed
    }

    @Test
    @Order(1)
    fun `verify ClassicBluetoothVescConnector is constructed properly`() {
        val device = vescBluetoothDevice()
        try {
            connector = VescConnectorFactory.createClassicBluetoothVescConnector(device, 1000)
        } catch (e: Exception) {
            fail("Failed to create Classic Bluetooth VESC connector", e)
        }
        assert(connector is ClassicBluetoothVescConnector)
    }

    @Test
    @Order(2)
    fun `send non-response commands to the Classic Bluetooth adapter on the VESC`() {
        assertDoesNotThrow("Failed to determine firmware version") {
            connector.determineFirmwareVersion()
        }
        assertDoesNotThrow("Failed to send RPM command") {
            connector.sendCommand(CommandProducer.CommandChoice.RPM, 0)
        }
        assertDoesNotThrow("Failed to send CURRENT command") {
            connector.sendCommand(CommandProducer.CommandChoice.CURRENT, 0.0)
        }
        assertDoesNotThrow("Failed to send HEARTBEAT command") {
            connector.sendCommand(CommandProducer.CommandChoice.HEARTBEAT)
        }
        assertDoesNotThrow("Failed to send REBOOT command") {
            connector.sendCommand(CommandProducer.CommandChoice.REBOOT)
        }
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