package net.bobacktech.bionicboarder.vesc.fw6_00

import net.bobacktech.bionicboarder.vesc.FirmwareVersionResponse
import kotlin.properties.Delegates

class FirmwareVersionResponse : FirmwareVersionResponse() {

    override var responseID: Int = 0

    // Backing fields using Delegates.notNull()
    private var _versionDescription: String by Delegates.notNull()
    private var _versionMajor: String by Delegates.notNull()
    private var _versionMinor: String by Delegates.notNull()

    // Public properties using lazy initialization
    override val versionDescription: String by lazy { _versionDescription }
    override val versionMajor: String by lazy { _versionMajor }
    override val versionMinor: String by lazy { _versionMinor }

    protected val payloadDataStartIndex: Int = 3

    override fun populateImpl(responsePacket: UByteArray) {
        var index = payloadDataStartIndex
        _versionMajor = (item<UByte>(responsePacket, index++)!!).toString()
        _versionMinor = (item<UByte>(responsePacket, index++)!!).toString()
        val hwName: String = item(responsePacket, index)!!
        index += hwName.length
        index += 15
        val hwType: UByte = item(responsePacket, index)!!
        _versionDescription = "Firmware Version: $_versionMajor.$_versionMinor\n" +
                "HW Name: $hwName\n" +
                "HW Type: $hwType"
    }

}