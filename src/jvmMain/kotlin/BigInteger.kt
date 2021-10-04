@file:JvmName("JvmBigIntegerKt")

package com.github.ephemient.kotlin.numeric

import kotlin.jvm.Throws

actual typealias BigInteger = java.math.BigInteger

actual object BigIntegers {
    actual val ZERO: BigInteger
        get() = BigInteger.ZERO
    actual val ONE: BigInteger
        get() = BigInteger.ONE
    actual val TEN: BigInteger
        get() = BigInteger.TEN
}

@Throws(NumberFormatException::class)
actual fun String.toBigInteger(radix: Int): BigInteger = BigInteger(this, radix)

actual fun Long.toBigInteger(): BigInteger = BigInteger.valueOf(this)

actual fun ULong.toBigInteger(): BigInteger {
    val long = this.toLong()
    return if (long < 0) {
        -Long.MIN_VALUE.toBigInteger() + (long xor Long.MIN_VALUE).toBigInteger()
    } else long.toBigInteger()
}

actual fun Int.toBigInteger(): BigInteger = toLong().toBigInteger()

actual fun UInt.toBigInteger(): BigInteger = toLong().toBigInteger()
