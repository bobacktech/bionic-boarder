package net.bobacktech.bionicboarder.vesc

import net.bobacktech.bionicboarder.vesc.fw6_00.Command
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class CommandProducerTest {

    private class TestCommandProducer : CommandProducer() {
        override val commandMap: HashMap<CommandChoice, net.bobacktech.bionicboarder.vesc.Command<*>> = hashMapOf(
            CommandProducer.CommandChoice.STATE to Command.State(),
            CommandProducer.CommandChoice.RPM to Command.RPM()
        )
    }

    private var commandProducer: TestCommandProducer = TestCommandProducer()

    @Test
    @DisplayName("verify that FW 6.00 State command packet is produced")
    fun testStateCommandProduction() {
        val packet: UByteArray = commandProducer(CommandProducer.CommandChoice.STATE)
        assert(packet[2] == Command.State().ID)
    }

    @Test
    @DisplayName("verify that FW 6.00 RPM command packet is produced")
    fun testRPMCommandProduction() {
        val packet: UByteArray = commandProducer(CommandProducer.CommandChoice.RPM, 1000)
        assert(packet[2] == Command.RPM().ID)
    }
}