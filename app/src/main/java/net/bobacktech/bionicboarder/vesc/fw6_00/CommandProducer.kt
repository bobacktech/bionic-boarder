package net.bobacktech.bionicboarder.vesc.fw6_00

import net.bobacktech.bionicboarder.vesc.CommandProducer

/**
 * This class represents a [CommandProducer] for the VESC firmware version 6.00. It associates the command choice with the
 * instance of the command object for the given specific firmware version.
 */
class CommandProducer : CommandProducer() {
    override val commandMap: HashMap<CommandChoice, net.bobacktech.bionicboarder.vesc.Command<*>> = hashMapOf(
        CommandChoice.FW_VERSION to Command.FirmwareVersion(),
        CommandChoice.STATE to Command.State(),
        CommandChoice.IMU_STATE to Command.IMUState(),
        CommandChoice.HEARTBEAT to Command.Heartbeat(),
        CommandChoice.REBOOT to Command.Reboot(),
        CommandChoice.RPM to Command.RPM(),
        CommandChoice.CURRENT to Command.Current(),
    )
}