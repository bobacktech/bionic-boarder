package net.bobacktech.bionicboarder.vesc.fw6_00

import net.bobacktech.bionicboarder.vesc.IMUStateResponse
import kotlin.properties.Delegates


class IMUStateReponse : IMUStateResponse() {

    override val responseID: Int = 65
    override val responseByteLength: Int = 3 + 68 // 3 bytes for the header

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
        // Skip the first 5 bytes of the response packet: 3 header bytes and 2 bytes for the mask
        _roll = item(responsePacket, 5, 4)!!
        _pitch = item(responsePacket, 9, 4)!!
        _yaw = item(responsePacket, 13, 4)!!
        _accelX = item(responsePacket, 17, 4)!!
        _accelY = item(responsePacket, 21, 4)!!
        _accelZ = item(responsePacket, 25, 4)!!
        _gyroX = item(responsePacket, 29, 4)!!
        _gyroY = item(responsePacket, 33, 4)!!
        _gyroZ = item(responsePacket, 37, 4)!!
        _magX = item(responsePacket, 41, 4)!!
        _magY = item(responsePacket, 45, 4)!!
        _magZ = item(responsePacket, 49, 4)!!
        _quat1 = item(responsePacket, 53, 4)!!
        _quat2 = item(responsePacket, 57, 4)!!
        _quat3 = item(responsePacket, 61, 4)!!
        _quat4 = item(responsePacket, 65, 4)!!
    }
}