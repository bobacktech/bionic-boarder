package net.bobacktech.bionicboarder.vesc

/**
 * Response from the VESC Firmware Version command that contains the firmware version information
 * of the VESC controller.
 */
abstract class FirmwareVersionResponse : Response() {

    abstract val versionDescription: String
    abstract val versionMajor: String
    abstract val versionMinor: String

}