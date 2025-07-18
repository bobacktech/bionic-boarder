package net.bobacktech.bionicboarder.csf

import net.bobacktech.bionicboarder.vesc.IMUStateResponse
import java.util.concurrent.ConcurrentLinkedDeque

data class VescImuStateTimed(val vescImuState: IMUStateResponse, val timestamp_ms: Long)
data class SmartphoneImuTimed(val imuMagnitude: Double, val timestamp_ms: Long)

typealias VescImuBuffer = ConcurrentLinkedDeque<VescImuStateTimed>
typealias SmartphoneImuBuffer = ConcurrentLinkedDeque<SmartphoneImuTimed>

fun VescImuBuffer.findVESCIteratorAtOrBeforeTimestamp(targetTimestamp: Long): Pair<VescImuStateTimed, Iterator<VescImuStateTimed>>? {
    val iterator: MutableIterator<VescImuStateTimed> = descendingIterator()
    while (iterator.hasNext()) {
        val element = iterator.next()
        if (element.timestamp_ms <= targetTimestamp) {
            return Pair(element, iterator)
        }
    }
    return null
}

fun SmartphoneImuBuffer.findSmartphoneIteratorAtOrBeforeTimestamp(targetTimestamp: Long): Pair<SmartphoneImuTimed, Iterator<SmartphoneImuTimed>>? {
    val iterator: MutableIterator<SmartphoneImuTimed> = descendingIterator()
    while (iterator.hasNext()) {
        val element = iterator.next()
        if (element.timestamp_ms <= targetTimestamp) {
            return Pair(element, iterator)
        }
    }
    return null
}