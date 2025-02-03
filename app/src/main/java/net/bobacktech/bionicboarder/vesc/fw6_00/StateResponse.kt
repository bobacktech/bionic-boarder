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

        // Starting at 3 assuming start_byte, LENGTH_HIGH and LENGTH_LOW, and packet_id
        var index = 3

        _mosfetTemp = item(responsePacket, index, 2, 10.0)!!
        index = 5
        _motorTemp = item(responsePacket, index, 2, 10.0)!!
        index = 7
        _motorCurrent = item(responsePacket, index, 4, 100.0)!!
        index = 23
        _dutyCycle = item(responsePacket, index, 2, 1000.0)!!
        index = 25
        _rpm = item(responsePacket, index, 4, 1.0)!!
        index = 29
        _inputVoltage = item(responsePacket, index, 2, 10.0)!!
        index = 39
        _wattHours = item(responsePacket, index, 4, 10000.0)!!
        index = 55
        val faultCode: UByte = item(responsePacket, index, 1)!!

        var f: FAULTS = FAULTS.FAULT_CODE_NONE
        when (faultCode!!.toInt()) {
            0 -> f = FAULTS.FAULT_CODE_NONE
            1 -> f = FAULTS.FAULT_CODE_OVER_VOLTAGE
            2 -> f = FAULTS.FAULT_CODE_UNDER_VOLTAGE
            3 -> f = FAULTS.FAULT_CODE_DRV
            4 -> f = FAULTS.FAULT_CODE_ABS_OVER_CURRENT
            5 -> f = FAULTS.FAULT_CODE_OVER_TEMP_FET
            6 -> f = FAULTS.FAULT_CODE_OVER_TEMP_MOTOR
            7 -> f = FAULTS.FAULT_CODE_GATE_DRIVER_OVER_VOLTAGE
            8 -> f = FAULTS.FAULT_CODE_GATE_DRIVER_UNDER_VOLTAGE
            9 -> f = FAULTS.FAULT_CODE_MCU_UNDER_VOLTAGE
            10 -> f = FAULTS.FAULT_CODE_BOOTING_FROM_WATCHDOG_RESET
            11 -> f = FAULTS.FAULT_CODE_ENCODER_SPI
            12 -> f = FAULTS.FAULT_CODE_ENCODER_SINCOS_BELOW_MIN_AMPLITUDE
            13 -> f = FAULTS.FAULT_CODE_ENCODER_SINCOS_ABOVE_MAX_AMPLITUDE
            14 -> f = FAULTS.FAULT_CODE_FLASH_CORRUPTION
            15 -> f = FAULTS.FAULT_CODE_HIGH_OFFSET_CURRENT_SENSOR_1
            16 -> f = FAULTS.FAULT_CODE_HIGH_OFFSET_CURRENT_SENSOR_2
            17 -> f = FAULTS.FAULT_CODE_HIGH_OFFSET_CURRENT_SENSOR_3
            18 -> f = FAULTS.FAULT_CODE_UNBALANCED_CURRENTS
            19 -> f = FAULTS.FAULT_CODE_BRK
            20 -> f = FAULTS.FAULT_CODE_RESOLVER_LOT
            21 -> f = FAULTS.FAULT_CODE_RESOLVER_DOS
            22 -> f = FAULTS.FAULT_CODE_RESOLVER_LOS
            23 -> f = FAULTS.FAULT_CODE_FLASH_CORRUPTION_APP_CFG
            24 -> f = FAULTS.FAULT_CODE_FLASH_CORRUPTION_MC_CFG
            25 -> f = FAULTS.FAULT_CODE_ENCODER_NO_MAGNET
            26 -> f = FAULTS.FAULT_CODE_ENCODER_MAGNET_TOO_STRONG
            27 -> f = FAULTS.FAULT_CODE_PHASE_FILTER
            28 -> f = FAULTS.FAULT_CODE_ENCODER_FAULT
            29 -> f = FAULTS.FAULT_CODE_LV_OUTPUT_FAULT
        }
        _fault = f.toString()
    }

    enum class FAULTS {
        FAULT_CODE_NONE,
        FAULT_CODE_OVER_VOLTAGE,
        FAULT_CODE_UNDER_VOLTAGE,
        FAULT_CODE_DRV,
        FAULT_CODE_ABS_OVER_CURRENT,
        FAULT_CODE_OVER_TEMP_FET,
        FAULT_CODE_OVER_TEMP_MOTOR,
        FAULT_CODE_GATE_DRIVER_OVER_VOLTAGE,
        FAULT_CODE_GATE_DRIVER_UNDER_VOLTAGE,
        FAULT_CODE_MCU_UNDER_VOLTAGE,
        FAULT_CODE_BOOTING_FROM_WATCHDOG_RESET,
        FAULT_CODE_ENCODER_SPI,
        FAULT_CODE_ENCODER_SINCOS_BELOW_MIN_AMPLITUDE,
        FAULT_CODE_ENCODER_SINCOS_ABOVE_MAX_AMPLITUDE,
        FAULT_CODE_FLASH_CORRUPTION,
        FAULT_CODE_HIGH_OFFSET_CURRENT_SENSOR_1,
        FAULT_CODE_HIGH_OFFSET_CURRENT_SENSOR_2,
        FAULT_CODE_HIGH_OFFSET_CURRENT_SENSOR_3,
        FAULT_CODE_UNBALANCED_CURRENTS,
        FAULT_CODE_BRK,
        FAULT_CODE_RESOLVER_LOT,
        FAULT_CODE_RESOLVER_DOS,
        FAULT_CODE_RESOLVER_LOS,
        FAULT_CODE_FLASH_CORRUPTION_APP_CFG,
        FAULT_CODE_FLASH_CORRUPTION_MC_CFG,
        FAULT_CODE_ENCODER_NO_MAGNET,
        FAULT_CODE_ENCODER_MAGNET_TOO_STRONG,
        FAULT_CODE_PHASE_FILTER,
        FAULT_CODE_ENCODER_FAULT,
        FAULT_CODE_LV_OUTPUT_FAULT
    }

}