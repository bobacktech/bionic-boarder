package net.bobacktech.bionicboarder.csf

import net.bobacktech.bionicboarder.vesc.IMUStateResponse
import java.util.concurrent.ConcurrentLinkedDeque

data class VescImuStateTimed(val vescImuState: IMUStateResponse, val timestamp_ms: Long)
data class SmartphoneImuTimed(val imuMagnitude: Double, val timestamp_ms: Long)

typealias VescImuBuffer = ConcurrentLinkedDeque<VescImuStateTimed>
typealias SmartphoneImuBuffer = ConcurrentLinkedDeque<SmartphoneImuTimed>