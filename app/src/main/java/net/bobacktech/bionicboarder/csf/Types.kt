package net.bobacktech.bionicboarder.csf

import net.bobacktech.bionicboarder.vesc.IMUStateResponse
import net.bobacktech.bionicboarder.vesc.StateResponse
import java.util.concurrent.ConcurrentLinkedDeque


data class VescStateTimed(val vescState: StateResponse, val timestamp_ms: Long)
data class VescImuStateTimed(val vescImuState: IMUStateResponse, val timestamp_ms: Long)
data class SmartphoneImuTimed(val imuMagnitude: Double, val timestamp_ms: Long)

typealias VescStateBuffer = ConcurrentLinkedDeque<VescStateTimed>
typealias VescImuBuffer = ConcurrentLinkedDeque<VescImuStateTimed>
typealias SmartphoneImuBuffer = ConcurrentLinkedDeque<SmartphoneImuTimed>