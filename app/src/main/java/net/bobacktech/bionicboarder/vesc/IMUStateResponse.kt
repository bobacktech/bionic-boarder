package net.bobacktech.bionicboarder.vesc

abstract class IMUStateResponse : Response() {

    abstract val accelX: Float
    abstract val accelY: Float
    abstract val accelZ: Float
    abstract val roll: Float
    abstract val pitch: Float
    abstract val yaw: Float
    abstract val gyroX: Float
    abstract val gyroY: Float
    abstract val gyroZ: Float
    abstract val magX: Float
    abstract val magY: Float
    abstract val magZ: Float
    abstract val quat1: Float
    abstract val quat2: Float
    abstract val quat3: Float
    abstract val quat4: Float

}