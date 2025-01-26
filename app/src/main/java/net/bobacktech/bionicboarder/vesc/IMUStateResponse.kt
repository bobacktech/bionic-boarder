package net.bobacktech.bionicboarder.vesc

abstract class IMUStateResponse : Response() {

    abstract val accelX: Float
    abstract val accelY: Float
    abstract val roll: Float
    abstract val pitch: Float
    abstract val yaw: Float
    abstract val gyroX: Float
    abstract val gyroY: Float
    abstract val gyroZ: Float
    abstract val magX: Float
    abstract val magY: Float
    abstract val magZ: Float

}