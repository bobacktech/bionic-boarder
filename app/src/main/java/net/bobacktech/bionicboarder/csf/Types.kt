package net.bobacktech.bionicboarder.csf

import net.bobacktech.bionicboarder.vesc.IMUStateResponse
import java.util.concurrent.ConcurrentLinkedDeque

data class VescImuStateTimed(val vescImuState: IMUStateResponse, val timestamp_ms: Long)
data class SmartphoneImuTimed(val imuMagnitude: Double, val timestamp_ms: Long)

typealias VescImuBuffer = ConcurrentLinkedDeque<VescImuStateTimed>
typealias SmartphoneImuBuffer = ConcurrentLinkedDeque<SmartphoneImuTimed>

/**
 * Finds the first element in the VESC IMU buffer that has a timestamp less than or equal to the target timestamp,
 * and returns it along with an iterator that can be used to traverse the remaining elements in descending order.
 *
 * @param targetTimestamp The timestamp to search for.
 * @return A pair containing the found element and an iterator, or null if no such element exists.
 */
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

/**
 * Finds the first element in the Smartphone IMU buffer that has a timestamp less than or equal to the target timestamp,
 * and returns it along with an iterator that can be used to traverse the remaining elements in descending order.
 *
 * @param targetTimestamp The timestamp to search for.
 * @return A pair containing the found element and an iterator, or null if no such element exists.
 */
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