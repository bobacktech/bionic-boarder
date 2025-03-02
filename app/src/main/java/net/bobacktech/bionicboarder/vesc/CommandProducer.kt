package net.bobacktech.bionicboarder.vesc

/**
 * This class represents an abstract interface for a Command producer that is used for any firmware version of the VESC. It enables the formation of VESC
 * queries by other sections of the code without explicitly knowing the VESC firmware version.
 */
abstract class CommandProducer {

    /**
     *  [CommandChoice] contains the names of queries for the commanding the VESC to do something.
     */
    enum class CommandChoice {
        FW_VERSION,
        STATE,
        IMU_STATE,
        HEARTBEAT,
        REBOOT,
        RPM,
        CURRENT
    }

    /**
     *  This map contains the command objects for the specific firmware version implementation of [CommandProducer], that can be sent to the VESC.
     */
    protected abstract val commandMap: HashMap<CommandChoice, Command<*>>


    /**
     *  This method invokes the form method of the command object for the given [CommandChoice] and data.
     */
    @Suppress("UNCHECKED_CAST")
    operator fun <DATA> invoke(qt: CommandChoice, data: DATA): UByteArray {
        val command = commandMap[qt] as Command<DATA>
        return command.form(data)
    }

    /**
     *  This method invokes the form method of the command object for the given [CommandChoice].
     */
    operator fun invoke(qt: CommandChoice): UByteArray {
        val command = commandMap[qt] as Command<*>
        return command.form()
    }
}