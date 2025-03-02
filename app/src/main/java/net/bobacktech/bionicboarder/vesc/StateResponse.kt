package net.bobacktech.bionicboarder.vesc

/**
 * Response from the VESC State command that contains the current state of the VESC controller that
 * is relevant to the Bionic Boarder app.
 */
abstract class StateResponse : Response() {

    abstract val mosfetTemp: Float
    abstract val motorTemp: Float
    abstract val motorCurrent: Float
    abstract val rpm: Int
    abstract val dutyCycle: Float
    abstract val wattHours: Float
    abstract val inputVoltage: Float
    abstract val fault: String

}