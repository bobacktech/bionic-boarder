package net.bobacktech.bionicboarder.vesc


/**
 *  This class represents an abstraction of a query for some information from the VESC or to make
 *  the VESC take some action.  It provides an interface to create the query as an unsigned byte
 *  array that will be sent to the VESC.
 */
abstract class Query<DATA> {

    /**
     *  This is the ID required by the VESC for a given query
     */
    abstract val queryID: UByte

    /**
     *  This method formulates the VESC query and returns a unsigned byte array
     *  to be sent to the VESC. Not all queries require a data item, which is why
     *  the default value is set to null.
     */
    fun form(data: DATA? = null): UByteArray {
        val buffer = createIDAndDataByteArray(data)
        return packetize(buffer)
    }

    /**
     *  This method is used to prepare the unsigned byte array that contains the [queryID]
     *  and any data if needed for the query.
     */
    protected abstract fun createIDAndDataByteArray(data: DATA? = null): UByteArray

    /**
     *  This combines the VESC header, the result of [createIDAndDataByteArray], and the computed crc value
     *  into one uniform unsigned byte array that represents the actual query sent to the VESC.
     */
    protected abstract fun packetize(data: UByteArray): UByteArray

    /**
     *  This array contains the values used to calculate the CRC value for the query.
     */
    protected abstract val crc16Table: ShortArray

}