package net.bobacktech.bionicboarder.vesc

abstract class FirmwareVersionResponse : Response() {

    abstract val versionDescription: String
    abstract val versionMajor: String
    abstract val versionMinor: String
}