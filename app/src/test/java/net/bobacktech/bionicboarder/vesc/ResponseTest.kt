package net.bobacktech.bionicboarder.vesc

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ResponseTest : Response() {

    override var responseID: Int = 0
        get() = TODO("Implementation not needed")

    override fun populateImpl(responsePacket: UByteArray) {
        TODO("Implementation not needed")
    }

    private val data = ubyteArrayOf(
        0x42u, 0x42u, 0x42u, 0x42u,
        0x48u, 0x65u, 0x6Cu, 0x6Cu, 0x6Fu, 0x00u,
        0x11u, 0x22u, 0x33u, 0x44u,
        // For Float32 and Float16 test
        0x64u, 0x32u, 0x14u, 0x0Au
    )

    @Test
    fun testString() {
        var result = this.item<String>(data, 4)
        assert(result == "Hello")
        result = this.item<String>(data, 4, 5)
        assert(result == "Hello")
    }

    @Test
    fun testByte() {
        val result = this.item<Byte>(data, 2)
        assert(result == 0x42.toByte())
    }

    @Test
    fun testUByte() {
        val result = this.item<UByte>(data, 0)
        assert(result == 0x42.toUByte())
    }

    @Test
    fun testUShort() {
        val result = this.item<UShort>(data, 12)
        assert(result == 0x3344.toUShort())
    }

    @Test
    fun testInt() {
        val result = this.item<Int>(data, 10)
        assert(result == 0x11223344)
    }

    @Test
    fun testUInt() {
        val result = this.item<UInt>(data, 10)
        assert(result == 0x11223344.toUInt())
    }

    @Test
    fun testFloat() {
        val result = this.item<Float>(data, 14, 4, 1000.0)
        val q = (0x6432140A / 1000.0).toFloat()
        assert(result == q)
        val q1 = (0x6432 / 10.0).toFloat()
        val result1 = this.item<Float>(data, 14, 2, 10.0)
        assert(result1 == q1)
    }
}
