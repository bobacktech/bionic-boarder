package net.bobacktech.bionicboarder.vesc

import net.bobacktech.bionicboarder.vesc.fw6_00.FirmwareVersionResponse as FW600FirmwareVersion
import net.bobacktech.bionicboarder.vesc.fw6_00.IMUStateReponse as FW600IMUState
import net.bobacktech.bionicboarder.vesc.fw6_00.StateResponse as FW600State

/**
 * This class represents an abstract contract for the communication between the app and the VESC. The
 * specific implementation of the communication channel is left to the child class.
 */
abstract class Connector {

    /**
     * The maximum time to wait for a response from the VESC in milliseconds.
     */
    protected abstract val responseTimeout_ms: Int

    /**
     * Exception thrown when the response from the VESC is not received within the [responseTimeout_ms].
     */
    class ResponseTimeoutException(message: String) : Exception(message)

    /**
     * Exception thrown when the firmware version is not supported.
     */
    class FirmwareVersionNotSupportedException(message: String) : Exception(message)

    /**
     * Enum representing the firmware versions supported by the connector.
     */
    enum class FirmwareVersion {
        FW_6_00;

        override fun toString(): String {
            return super.toString().replace("FW_", "").replace('_', '.')
        }
    }

    /**
     * The firmware version of the VESC on the board.
     * This property must be set in the [determineFirmwareVersion] method implemented in the child class.
     */
    abstract val firmwareVersion: FirmwareVersion

    /**
     * The command producer for [firmwareVersion] of the VESC.
     * This property must be set in the [determineFirmwareVersion] method implemented in the child class.
     */
    protected abstract val qp: CommandProducer

    /**
     * This method determines the firmware version of the VESC that the app is connected to.
     * This method must set the [firmwareVersion] and the [qp] properties.
     * @throws [FirmwareVersionNotSupportedException] if the firmware version is not supported.
     */
    @Throws(FirmwareVersionNotSupportedException::class)
    abstract fun determineFirmwareVersion()


    /**
     * This method sends a byte packet over the communication channel to the VESC.
     * @param packet The byte packet to be sent.
     */
    protected abstract fun sendBytes(packet: UByteArray)

    /**
     * This method reads [numBytes] bytes from the communication channel to the VESC.
     * @param numBytes The number of bytes to read.
     * @return The bytes read from the communication channel.
     */
    protected abstract fun readBytes(numBytes: Int): UByteArray

    /**
     * This method sends a command packet to the VESC and reads the response bytes from the VESC. It
     * then populates the response object with the data received from the VESC.
     * @param qc The command choice to be used for the request.
     * @return A [Response] object populated with the data received from the VESC.
     */
    inline fun <reified R : Response> requestResponse(qc: CommandProducer.CommandChoice): R {
        sendCommand(qc)
        val response: R
        val rClazz = R::class.java
        when (firmwareVersion) {
            FirmwareVersion.FW_6_00 -> {
                response = when {
                    rClazz.isAssignableFrom(FW600FirmwareVersion::class.java) -> FW600FirmwareVersion()
                    rClazz.isAssignableFrom(FW600State::class.java) -> FW600State()
                    rClazz.isAssignableFrom(FW600IMUState::class.java) -> FW600IMUState()
                    else -> throw IllegalStateException("Unknown response class")
                } as R
            }
        }
        val rawResponse = `access$readBytes`(response.responseByteLength)
        response.populate(rawResponse)
        return response
    }

    /**
     * This method sends a command to the VESC to perform some action.
     * @param qc The command choice to be sent.
     * @param data Optional data to be sent with the command.
     */
    fun sendCommand(qc: CommandProducer.CommandChoice, data: Number? = null) {
        val packet = if (data == null) qp(qc) else qp(qc, data)
        sendBytes(packet)
    }

    @PublishedApi
    internal fun `access$readBytes`(numBytes: Int) = readBytes(numBytes)

    /**
     * This method performs the necessary things to terminate the communication channel for this [Connector] with the VESC.
     */
    abstract fun shutdown()
}