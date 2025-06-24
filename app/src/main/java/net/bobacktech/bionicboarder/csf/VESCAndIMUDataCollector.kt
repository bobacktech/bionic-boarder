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

/**
 * This class is primarily responsible for being a collector of VESC and IMU data during a riding mission.
 *
 * @property connector The connector used to communicate with the VESC.
 * @property mc The mission clock used to timestamp the data.
 * @property vescBuffer The buffer for storing VESC state responses.
 * @property imuBuffer The buffer for storing IMU state responses.
 */
class VESCAndIMUDataCollector(
    private val connector: Connector,
    private val mc: MissionClock,
    private val vescBuffer: ConcurrentLinkedDeque<Pair<StateResponse, Long>>,
    private val imuBuffer: ConcurrentLinkedDeque<Pair<IMUStateResponse, Long>>
) {

    /**
     * Starts collecting data from the VESC and IMU in a coroutine.
     * The data is collected until the coroutine is cancelled or an exception occurs.
     */
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