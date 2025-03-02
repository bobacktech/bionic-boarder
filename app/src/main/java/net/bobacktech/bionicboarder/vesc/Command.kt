package net.bobacktech.bionicboarder.vesc


/**
 *  This class represents an abstraction of a command for some information from the VESC or to make
 *  the VESC take some action.  It provides an interface to create the command as an unsigned byte
 *  array that will be sent to the VESC.
 */
abstract class Command<DATA> {

    /**
     *  This is the ID required by the VESC for a given command
     */
    abstract val ID: UByte

    /**
     *  This method formulates the VESC command and returns a unsigned byte array
     *  to be sent to the VESC. Not all queries require a data item, which is why
     *  the default value is set to null.
     */
    fun form(data: DATA? = null): UByteArray {
        val buffer = createIDAndDataByteArray(data)
        return packetize(buffer)
    }

    /**
     *  This method is used to prepare the unsigned byte array that contains the [ID]
     *  and any data if needed for the command.
     */
    protected abstract fun createIDAndDataByteArray(data: DATA? = null): UByteArray

    /**
     *  This combines the VESC header, the result of [createIDAndDataByteArray], and the computed crc value
     *  into one uniform unsigned byte array that represents the actual command sent to the VESC.
     */
    protected abstract fun packetize(data: UByteArray): UByteArray

    /**
     *  This array contains the values used to calculate the CRC value for the command.
     */
    protected abstract val crc16Table: ShortArray

}