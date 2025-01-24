package net.bobacktech.bionicboarder.vesc

/**
 * This class represents an abstract interface for a Query producer that is used for any firmware version of the VESC. It enables the formation of VESC
 * queries by other sections of the code without explicitly knowing the VESC firmware version.
 */
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

    /**
     *  This map contains the query objects for the specific firmware version implementation of [QueryProducer], that can be sent to the VESC.
     */
    protected abstract val queryMap: HashMap<QueryChoice, Query<*>>


    /**
     *  This method invokes the form method of the query object for the given [QueryChoice] and data.
     */
    @Suppress("UNCHECKED_CAST")
    operator fun <DATA> invoke(qt: QueryChoice, data: DATA): UByteArray {
        val query = queryMap[qt] as Query<DATA>
        return query.form(data)
    }

    /**
     *  This method invokes the form method of the query object for the given [QueryChoice].
     */
    operator fun invoke(qt: QueryChoice): UByteArray {
        val query = queryMap[qt] as Query<*>
        return query.form()
    }
}