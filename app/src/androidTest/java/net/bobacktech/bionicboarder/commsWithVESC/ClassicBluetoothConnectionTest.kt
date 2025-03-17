package net.bobacktech.bionicboarder.commsWithVESC

import net.bobacktech.bionicboarder.TestBase
import net.bobacktech.bionicboarder.comms.ClassicBluetoothVescConnector
import net.bobacktech.bionicboarder.comms.VescConnectorFactory
import net.bobacktech.bionicboarder.vesc.CommandProducer
import net.bobacktech.bionicboarder.vesc.Connector
import net.bobacktech.bionicboarder.vesc.FirmwareVersionResponse
import net.bobacktech.bionicboarder.vesc.StateResponse
import net.bobacktech.bionicboarder.vesc.fw6_00.IMUStateReponse
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
            connector = VescConnectorFactory.createClassicBluetoothVescConnector(device, 10000)
        } catch (e: Exception) {
            fail("Failed to create Classic Bluetooth VESC connector", e)
        }
        assert(connector is ClassicBluetoothVescConnector)
        assertDoesNotThrow("Failed to determine firmware version") {
            connector.determineFirmwareVersion()
        }
    }

    @Test
    @Order(2)
    fun `send response commands to the Classic Bluetooth adapter on the VESC`() {
        val firmwareVersionResponse: FirmwareVersionResponse =
            connector.requestResponse(CommandProducer.CommandChoice.FW_VERSION)
        assert(firmwareVersionResponse.versionMajor == "6") { "Failed to get firmware version major" }
        val stateResponse: StateResponse =
            connector.requestResponse(CommandProducer.CommandChoice.STATE)
        assert(stateResponse.rpm > 0) { "Failed to get RPM value" }
        val imuStateResponse: IMUStateReponse =
            connector.requestResponse(CommandProducer.CommandChoice.IMU_STATE)
        assert(imuStateResponse.accelX != 0f) { "Failed to get IMU state response ID" }
    }

    @Test
    @Order(3)
    fun `send non-response commands to the Classic Bluetooth adapter on the VESC`() {
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