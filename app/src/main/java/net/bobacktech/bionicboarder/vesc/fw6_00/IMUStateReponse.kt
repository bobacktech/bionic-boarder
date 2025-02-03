package net.bobacktech.bionicboarder.vesc.fw6_00

import net.bobacktech.bionicboarder.vesc.IMUStateResponse
import kotlin.properties.Delegates


class IMUStateReponse : IMUStateResponse() {

    override var responseID: Int = 65

    // Backing properties using Delegates.notNull()
    private var _accelX: Float by Delegates.notNull()
    private var _accelY: Float by Delegates.notNull()
    private var _accelZ: Float by Delegates.notNull()
    private var _roll: Float by Delegates.notNull()
    private var _pitch: Float by Delegates.notNull()
    private var _yaw: Float by Delegates.notNull()
    private var _gyroX: Float by Delegates.notNull()
    private var _gyroY: Float by Delegates.notNull()
    private var _gyroZ: Float by Delegates.notNull()
    private var _magX: Float by Delegates.notNull()
    private var _magY: Float by Delegates.notNull()
    private var _magZ: Float by Delegates.notNull()
    private var _quat1: Float by Delegates.notNull()
    private var _quat2: Float by Delegates.notNull()
    private var _quat3: Float by Delegates.notNull()
    private var _quat4: Float by Delegates.notNull()

    // Public read-only properties using lazy
    override val accelX: Float by lazy { _accelX }
    override val accelY: Float by lazy { _accelY }
    override val accelZ: Float by lazy { _accelZ }
    override val roll: Float by lazy { _roll }
    override val pitch: Float by lazy { _pitch }
    override val yaw: Float by lazy { _yaw }
    override val gyroX: Float by lazy { _gyroX }
    override val gyroY: Float by lazy { _gyroY }
    override val gyroZ: Float by lazy { _gyroZ }
    override val magX: Float by lazy { _magX }
    override val magY: Float by lazy { _magY }
    override val magZ: Float by lazy { _magZ }
    override val quat1: Float by lazy { _quat1 }
    override val quat2: Float by lazy { _quat2 }
    override val quat3: Float by lazy { _quat3 }
    override val quat4: Float by lazy { _quat4 }

    /**
     * Populate the response object with the data from the response packet
     * The IMU data starts at position 5 in the response packet.
     */
    override fun populateImpl(responsePacket: UByteArray) {
        var index = 5
        _roll = item(responsePacket, index, 4)!!
        index += 4
        _pitch = item(responsePacket, index, 4)!!
        index += 4
        _yaw = item(responsePacket, index, 4)!!
        index += 4
        _accelX = item(responsePacket, index, 4)!!
        index += 4
        _accelY = item(responsePacket, index, 4)!!
        index += 4
        _accelZ = item(responsePacket, index, 4)!!
        index += 4
        _gyroX = item(responsePacket, index, 4)!!
        index += 4
        _gyroY = item(responsePacket, index, 4)!!
        index += 4
        _gyroZ = item(responsePacket, index, 4)!!
        index += 4
        _magX = item(responsePacket, index, 4)!!
        index += 4
        _magY = item(responsePacket, index, 4)!!
        index += 4
        _magZ = item(responsePacket, index, 4)!!
        index += 4
        _quat1 = item(responsePacket, index, 4)!!
        index += 4
        _quat2 = item(responsePacket, index, 4)!!
        index += 4
        _quat3 = item(responsePacket, index, 4)!!
        index += 4
        _quat4 = item(responsePacket, index, 4)!!
    }

}