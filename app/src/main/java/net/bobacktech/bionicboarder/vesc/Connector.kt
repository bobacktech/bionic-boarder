package net.bobacktech.bionicboarder.vesc

import net.bobacktech.bionicboarder.vesc.fw6_00.FirmwareVersionResponse as FW600FirmwareVersion
import net.bobacktech.bionicboarder.vesc.fw6_00.IMUStateReponse as FW600IMUState
import net.bobacktech.bionicboarder.vesc.fw6_00.StateResponse as FW600State

/**
 * Abstract class representing a connector to the VESC (Vedder Electronic Speed Controller).
 * This class provides methods to determine the firmware version, send queries, and read responses from the VESC.
 */
abstract class Connector {
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
     * Abstract property representing the firmware version of the connector.
     * This property must be set in the [determineFirmwareVersion] method implemented in the child class.
     */
    abstract val firmwareVersion: FirmwareVersion

    /**
     * Abstract property representing the query producer of the connector.
     * This property must be set in the [determineFirmwareVersion] method implemented in the child class.
     */
    protected abstract val qp: QueryProducer

    /**
     * Abstract method to determine the firmware version of the VESC that the app is connected to.
     * This method must set the [firmwareVersion] and the [qp] properties.
     * @throws [FirmwareVersionNotSupportedException] if the firmware version is not supported.
     */
    @Throws(FirmwareVersionNotSupportedException::class)
    abstract fun determineFirmwareVersion()


    /**
     * Abstract method to send a query packet to the VESC to perform some action.
     * @param packet The query packet to be sent.
     */
    protected abstract fun sendQuery(packet: UByteArray)

    /**
     * Abstract method to read a response packet from the VESC.
     * @param msgSize The size of the response packet to be read.
     * @return The response packet read from the VESC.
     */
    protected abstract fun readResponse(msgSize: Int): UByteArray

    /**
     * Generic method to request data from the VESC. The method sends a query packet to the VESC and reads the response. It
     * then populates the response object with the data received from the VESC.
     * @param qc The query choice to be used for the request.
     * @return A [Response] object populated with the data received from the VESC.
     */
    @Suppress("UNCHECKED_CAST")
    fun <R : Response> requestData(qc: QueryProducer.QueryChoice): R {
        val packet = qp(qc)
        sendQuery(packet)
        val response: R
        val rClazz = Response::class.java
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
        val rawResponse = readResponse(response.responseByteLength)
        response.populate(rawResponse)
        return response
    }

    /**
     * This method sends a command to the VESC that does not to process a response from the VESC.
     * @param qc The query choice to be used for the command.
     * @param data Optional data to be sent with the command.
     */
    fun command(qc: QueryProducer.QueryChoice, data: Number? = null) {
        val packet = if (data == null) qp(qc) else qp(qc, data)
        sendQuery(packet)
    }
}