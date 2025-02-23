package net.bobacktech.bionicboarder.vesc.fw6_00

import net.bobacktech.bionicboarder.vesc.QueryProducer

/**
 * This class represents a [QueryProducer] for the VESC firmware version 6.00. It associates the query choice with the
 * instance of the query object for the given specific firmware version.
 */
class QueryProducer : QueryProducer() {
    override val queryMap: HashMap<QueryChoice, net.bobacktech.bionicboarder.vesc.Query<*>> = hashMapOf(
        QueryChoice.FW_VERSION to Query.FirmwareVersion(),
        QueryChoice.STATE to Query.State(),
        QueryChoice.IMU_STATE to Query.IMUState(),
        QueryChoice.HEARTBEAT to Query.Heartbeat(),
        QueryChoice.REBOOT to Query.Reboot(),
        QueryChoice.RPM to Query.RPM(),
        QueryChoice.CURRENT to Query.Current(),
    )
}