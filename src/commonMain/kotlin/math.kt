@file:Suppress("TooManyFunctions")

package com.github.ephemient.kotlin.numeric

@Throws(ArithmeticException::class)
expect infix fun Long.addExact(other: Long): Long

@Throws(ArithmeticException::class)
infix fun ULong.addExact(other: ULong): ULong {
    val result = this + other
    if (result < this) throw ArithmeticException("integer overflow")
    return result
}

@Throws(ArithmeticException::class)
expect infix fun Int.addExact(other: Int): Int

@Throws(ArithmeticException::class)
infix fun UInt.addExact(other: UInt): UInt = (this.toLong() + other.toLong()).toUIntExact()

@Throws(ArithmeticException::class)
expect infix fun Long.subtractExact(other: Long): Long

@Throws(ArithmeticException::class)
infix fun ULong.subtractExact(other: ULong): ULong = if (this < other) {
    throw ArithmeticException("integer overflow")
} else this - other

@Throws(ArithmeticException::class)
expect infix fun Int.subtractExact(other: Int): Int

@Throws(ArithmeticException::class)
infix fun UInt.subtractExact(other: UInt): UInt = if (this < other) {
    throw ArithmeticException("integer overflow")
} else this - other

@Throws(ArithmeticException::class)
expect infix fun Long.multiplyExact(other: Long): Long

@Suppress("ComplexCondition")
@Throws(ArithmeticException::class)
infix fun ULong.multiplyExact(other: ULong): ULong {
    val result = this * other
    val leadingZeros = this.countLeadingZeroBits() + other.countLeadingZeroBits()
    return if (
        leadingZeros < Long.SIZE_BITS - 1 ||
        leadingZeros == Long.SIZE_BITS - 1 && this != 0UL && result / this != other
    ) throw ArithmeticException("integer overflow") else result
}

@Throws(ArithmeticException::class)
expect infix fun Int.multiplyExact(other: Int): Int

@Throws(ArithmeticException::class)
infix fun UInt.multiplyExact(other: UInt): UInt = (this.toULong() * other.toULong()).toUIntExact()

infix fun Long.addSaturating(other: Long): Long {
    val result = this + other
    if ((this xor other).inv() and (this xor result) < 0) return if (this < 0) Long.MIN_VALUE else Long.MAX_VALUE
    return result
}

infix fun ULong.addSaturating(other: ULong): ULong {
    val result = this + other
    return if (result < this) ULong.MAX_VALUE else result
}

infix fun Int.addSaturating(other: Int): Int = (this.toLong() + other.toLong()).toIntSaturating()

infix fun UInt.addSaturating(other: UInt): UInt = (this.toLong() + other.toLong()).toUIntSaturating()

infix fun Long.subtractSaturating(other: Long): Long = when {
    other != Long.MIN_VALUE -> this addSaturating -other
    this < 0 -> this + Long.MIN_VALUE
    else -> Long.MAX_VALUE
}

infix fun ULong.subtractSaturating(other: ULong): ULong = if (this <= other) 0UL else this - other

infix fun Int.subtractSaturating(other: Int): Int = (this.toLong() - other.toLong()).toIntSaturating()

infix fun UInt.subtractSaturating(other: UInt): UInt = if (this <= other) 0U else this - other

infix fun Long.multiplySaturating(other: Long): Long {
    val result = this * other
    val limit = Long.MAX_VALUE + (this xor other ushr Long.SIZE_BITS - 1)
    val leadingZeros = this.countLeadingZeroBits() + this.inv().countLeadingZeroBits() +
        other.countLeadingZeroBits() + other.inv().countLeadingZeroBits()
    return when {
        leadingZeros > Long.SIZE_BITS + 1 -> result
        leadingZeros < Long.SIZE_BITS || this < 0L && other == Long.MIN_VALUE || this != 0L && result / this != other
        -> limit
        else -> result
    }
}

@Suppress("ComplexCondition")
infix fun ULong.multiplySaturating(other: ULong): ULong {
    val result = this * other
    val leadingZeros = this.countLeadingZeroBits() + other.countLeadingZeroBits()
    return if (
        leadingZeros < Long.SIZE_BITS - 1 ||
        leadingZeros == Long.SIZE_BITS - 1 && this != 0UL && result / this != other
    ) ULong.MAX_VALUE else result
}

infix fun Int.multiplySaturating(other: Int): Int = (this.toLong() * other.toLong()).toIntSaturating()

infix fun UInt.multiplySaturating(other: UInt): UInt = (this.toULong() * other.toULong()).toUIntSaturating()
