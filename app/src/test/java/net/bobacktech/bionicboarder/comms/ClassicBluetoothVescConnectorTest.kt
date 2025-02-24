package net.bobacktech.bionicboarder.comms

import android.bluetooth.BluetoothSocket
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import net.bobacktech.bionicboarder.vesc.Connector
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.InputStream
import java.io.OutputStream
import io.mockk.mockkStatic
import android.os.SystemClock

class ClassicBluetoothVescConnectorTest {

    @MockK
    private lateinit var mockBluetoothSocket: BluetoothSocket

    @MockK
    private lateinit var mockInputStream: InputStream

    @MockK
    private lateinit var mockOutputStream: OutputStream

    private lateinit var connector: ClassicBluetoothVescConnector
    private val responseTimeout = 1000

    private var mockTime = 0L

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        every { mockBluetoothSocket.inputStream } returns mockInputStream
        every { mockBluetoothSocket.outputStream } returns mockOutputStream
        connector = ClassicBluetoothVescConnector(mockBluetoothSocket, responseTimeout)
        mockkStatic(SystemClock::class)  // Mock the SystemClock class
        every { SystemClock.elapsedRealtime() } returns mockTime  // Initial mock value
    }

    @Test
    fun `determineFirmwareVersion should set correct firmware version for FW 6_00`() {
        // Arrange
        val expectedResponse = byteArrayOf(0, 0, 0, 6, 0, 0, 0, 0, 0, 0)
        every { mockInputStream.available() } returns 10
        every { mockInputStream.read(any(), any(), any()) } answers {
            val buffer = arg<ByteArray>(0)
            System.arraycopy(expectedResponse, 0, buffer, arg(1), arg(2))
            10
        }
        every { mockOutputStream.write(any<ByteArray>()) } just Runs

        // Act
        connector.determineFirmwareVersion()

        // Assert
        assertEquals(Connector.FirmwareVersion.FW_6_00, connector.firmwareVersion)
        verify { mockOutputStream.write(any<ByteArray>()) }
    }

    @Test
    fun `determineFirmwareVersion should throw exception for unsupported firmware`() {
        // Arrange
        val unsupportedVersion = byteArrayOf(0, 0, 0, 7, 0, 0, 0, 0, 0, 0)
        every { mockInputStream.available() } returns 10
        every { mockInputStream.read(any(), any(), any()) } answers {
            val buffer = arg<ByteArray>(0)
            System.arraycopy(unsupportedVersion, 0, buffer, arg(1), arg(2))
            10
        }
        every { mockOutputStream.write(any<ByteArray>()) } just Runs

        // Act & Assert
        assertThrows<Connector.FirmwareVersionNotSupportedException> {
            connector.determineFirmwareVersion()
        }
    }
}