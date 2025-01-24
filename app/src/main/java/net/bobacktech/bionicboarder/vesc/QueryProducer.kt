package net.bobacktech.bionicboarder.vesc

abstract class QueryProducer {

    /**
     *  [QueryChoice] contains the names of queries for the commanding the VESC to do something.
     */
    enum class QueryChoice {
        FW_VERSION,
        STATE,
        IMU_STATE,
        HEARTBEAT,
        REBOOT,
        RPM,
        CURRENT
    }

    protected abstract val queryMap: HashMap<QueryChoice, Query<*>>

    @Suppress("UNCHECKED_CAST")
    operator fun <DATA> invoke(qt: QueryChoice, data: DATA): UByteArray {
        val query = queryMap[qt] as Query<DATA>
        return query.form(data)
    }

    operator fun invoke(qt: QueryChoice): UByteArray {
        val query = queryMap[qt] as Query<*>
        return query.form()
    }
}