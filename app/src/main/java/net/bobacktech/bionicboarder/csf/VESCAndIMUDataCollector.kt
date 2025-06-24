package net.bobacktech.bionicboarder.csf

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import net.bobacktech.bionicboarder.utils.MissionClock
import net.bobacktech.bionicboarder.vesc.CommandProducer
import net.bobacktech.bionicboarder.vesc.Connector
import net.bobacktech.bionicboarder.vesc.IMUStateResponse
import net.bobacktech.bionicboarder.vesc.StateResponse
import net.bobacktech.bionicboarder.vesc.fw6_00.IMUStateReponse
import java.util.concurrent.ConcurrentLinkedDeque

class VESCAndIMUDataCollector(
    private val connector: Connector,
    private val mc: MissionClock,
    private val vescBuffer: ConcurrentLinkedDeque<Pair<StateResponse, Long>>,
    private val imuBuffer: ConcurrentLinkedDeque<Pair<IMUStateResponse, Long>>
) {

    suspend fun collectData() {
        while (currentCoroutineContext().isActive) {
            delay(1)
            try {
                val stateResponse: StateResponse =
                    connector.requestResponse(CommandProducer.CommandChoice.STATE)
                val imuStateResponse: IMUStateReponse =
                    connector.requestResponse(CommandProducer.CommandChoice.IMU_STATE)
                val ts = mc()
                vescBuffer.add(Pair(stateResponse, ts))
                imuBuffer.add(Pair(imuStateResponse, ts))
            } catch (e: Connector.ResponseTimeoutException) {
                // Log the exception or handle it as needed
                continue
            } catch (e: Exception) {
                // Log the exception or handle it as needed
                // Notify the user that error occurred and end the riding mission
                break
            }
        }
    }
}