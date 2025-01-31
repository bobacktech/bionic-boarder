package net.bobacktech.bionicboarder.vesc


abstract class Response {

    abstract var responseID: Int

    private var isPopulated = false

    fun populate(responsePacket: UByteArray) {
        if (isPopulated) {
            throw IllegalStateException("populate() can only be called once per instance of class Response")
        }
        populateImpl(responsePacket)
        isPopulated = true
    }

    protected abstract fun populateImpl(responsePacket: UByteArray)

    protected inline fun <reified T> item(
        data: UByteArray,
        startIndex: Int,
        nbytes: Int = 0,
        scale: Double = 0.0
    ): T? {
        var result: T? = null

        when (T::class) {
            String::class -> {
                if (nbytes > 0) {
                    result = String(
                        (data.sliceArray(startIndex..<(startIndex + nbytes)))
                            .toByteArray(), Charsets.UTF_8
                    ) as T
                } else {
                    val nullIndex =
                        data.withIndex().drop(startIndex).find { it.value == 0u.toUByte() }?.index
                    result = String(
                        (data.sliceArray(startIndex..<nullIndex!!)).toByteArray(),
                        Charsets.UTF_8
                    ) as T
                }
            }

            Byte::class -> result = data[startIndex].toByte() as T
            UByte::class -> result = data[startIndex] as T
            UShort::class -> {
                result = ((data[startIndex].toInt() shl 8).toUShort()
                        or data[startIndex + 1].toUShort()) as T
            }

            Int::class -> {
                result = ((data[startIndex].toInt() shl 24)
                        or (data[startIndex + 1].toInt() shl 16)
                        or (data[startIndex + 2].toInt() shl 8)
                        or (data[startIndex + 3].toInt())) as T
            }

            UInt::class -> {
                result = ((data[startIndex].toInt() shl 24)
                        or (data[startIndex + 1].toInt() shl 16)
                        or (data[startIndex + 2].toInt() shl 8)
                        or (data[startIndex + 3].toInt())).toUInt() as T
            }

            Float::class -> {
                var v = 0
                val bits = (nbytes * 8) - 8
                for (i in 0..<nbytes) {
                    v = v or (data[startIndex + i].toInt() shl bits - i * 8)
                }
                result = when {
                    scale > 1 -> (v / scale).toFloat() as T
                    else -> Float.fromBits(v) as T
                }
            }
        }
        return result
    }
}