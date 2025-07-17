package net.bobacktech.bionicboarder.csf

import net.bobacktech.bionicboarder.vesc.IMUStateResponse
import java.util.concurrent.ConcurrentLinkedDeque

data class VescImuStateTimed(val vescImuState: IMUStateResponse, val timestamp_ms: Long)
data class SmartphoneImuTimed(val imuMagnitude: Double, val timestamp_ms: Long)

typealias VescImuBuffer = ConcurrentLinkedDeque<VescImuStateTimed>
typealias SmartphoneImuBuffer = ConcurrentLinkedDeque<SmartphoneImuTimed>

fun VescImuBuffer.findIteratorAtOrNextOneAfterTimestamp(targetTimestamp: Long): Iterator<VescImuStateTimed>? {
    val iterator: MutableIterator<VescImuStateTimed> = descendingIterator()
    var lastIterator = iterator
    while (iterator.hasNext()) {
        val element = iterator.next()
        if (element.timestamp_ms < targetTimestamp) {
            return lastIterator
        }
        lastIterator = iterator
    }
    return null
}

fun SmartphoneImuBuffer.findIteratorAtOrNextOneAfterTimestamp(targetTimestamp: Long): Iterator<SmartphoneImuTimed>? {
    val iterator = descendingIterator()
    var lastIterator = iterator
    while (iterator.hasNext()) {
        val element = iterator.next()
        if (element.timestamp_ms < targetTimestamp) {
            return lastIterator
        }
        lastIterator = iterator
    }
    return null
}