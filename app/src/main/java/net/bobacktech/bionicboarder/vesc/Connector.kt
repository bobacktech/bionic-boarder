package net.bobacktech.bionicboarder.vesc

import net.bobacktech.bionicboarder.vesc.fw6_00.FirmwareVersionResponse as FW600FirmwareVersion
import net.bobacktech.bionicboarder.vesc.fw6_00.IMUStateReponse as FW600IMUState
import net.bobacktech.bionicboarder.vesc.fw6_00.StateResponse as FW600State

abstract class Connector {

    enum class FirmwareVersion {
        FW_6_00;

        override fun toString(): String {
            return super.toString().replace("FW_", "").replace('_', '.')
        }
    }

    lateinit var firmwareVersion: FirmwareVersion
        private set

    abstract val RESPONSE_TIMEOUT_MS: Int

    protected lateinit var qp: QueryProducer

    abstract fun determineFirmwareVersion()
    protected abstract fun sendQuery(packet: UByteArray)
    protected abstract fun readResponse(msgSize: Int): UByteArray

    @Suppress("UNCHECKED_CAST")
    fun <R : Response> requestData(qc: QueryProducer.QueryChoice): R {
        val packet = qp(qc)
        sendQuery(packet)
        val response: R
        val rClazz = Response::class.java
        when (firmwareVersion) {
            FirmwareVersion.FW_6_00 -> {
                response = when {
                    rClazz.isAssignableFrom(FW600FirmwareVersion::class.java) -> FW600FirmwareVersion() as R
                    rClazz.isAssignableFrom(FW600State::class.java) -> FW600State() as R
                    rClazz.isAssignableFrom(FW600IMUState::class.java) -> FW600IMUState() as R
                    else -> throw IllegalStateException("Unknown response class")
                }
            }
        }
        val rawResponse = readResponse(100)
        response.populate(rawResponse)
        return response
    }

    fun command(qc: QueryProducer.QueryChoice, data: Number? = null) {
        val packet = if (data == null) qp(qc) else qp(qc, data)
        sendQuery(packet)
    }
}