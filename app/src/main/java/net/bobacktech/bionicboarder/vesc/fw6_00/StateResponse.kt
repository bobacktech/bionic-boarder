package net.bobacktech.bionicboarder.vesc.fw6_00

import net.bobacktech.bionicboarder.vesc.StateResponse
import kotlin.properties.Delegates

class StateResponse : StateResponse() {
    override var responseID: Int = 4

    // Backing fields using Delegates.notNull()
    private var _mosfetTemp: Float by Delegates.notNull()
    private var _motorTemp: Float by Delegates.notNull()
    private var _motorCurrent: Float by Delegates.notNull()
    private var _rpm: Int by Delegates.notNull()
    private var _dutyCycle: Float by Delegates.notNull()
    private var _wattHours: Float by Delegates.notNull()
    private var _inputVoltage: Float by Delegates.notNull()
    private var _fault: String by Delegates.notNull()

    // Public properties using lazy initialization
    override val mosfetTemp: Float by lazy { _mosfetTemp }
    override val motorTemp: Float by lazy { _motorTemp }
    override val motorCurrent: Float by lazy { _motorCurrent }
    override val rpm: Int by lazy { _rpm }
    override val dutyCycle: Float by lazy { _dutyCycle }
    override val wattHours: Float by lazy { _wattHours }
    override val inputVoltage: Float by lazy { _inputVoltage }
    override val fault: String by lazy { _fault }

    override fun populateImpl(responsePacket: UByteArray) {
        TODO("Not yet implemented")
    }
}