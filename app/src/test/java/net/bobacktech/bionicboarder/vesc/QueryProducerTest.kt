package net.bobacktech.bionicboarder.vesc

import net.bobacktech.bionicboarder.vesc.fw6_00.Query
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class QueryProducerTest {

    private class TestQueryProducer : QueryProducer() {
        override val queryMap: HashMap<QueryChoice, net.bobacktech.bionicboarder.vesc.Query<*>> = hashMapOf(
            QueryProducer.QueryChoice.STATE to Query.State(),
            QueryProducer.QueryChoice.RPM to Query.RPM()
        )
    }

    private var queryProducer: TestQueryProducer = TestQueryProducer()

    @Test
    @DisplayName("verify that FW 6.00 State query packet is produced")
    fun testStateQueryProduction() {
        val packet: UByteArray = queryProducer(QueryProducer.QueryChoice.STATE)
        assert(packet[2] == Query.State().queryID)
    }

    @Test
    @DisplayName("verify that FW 6.00 RPM query packet is produced")
    fun testRPMQueryProduction() {
        val packet: UByteArray = queryProducer(QueryProducer.QueryChoice.RPM, 1000)
        assert(packet[2] == Query.RPM().queryID)
    }
}