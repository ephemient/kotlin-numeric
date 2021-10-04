package com.github.ephemient.kotlin.numeric

@Throws(ArithmeticException::class)
actual fun Long.toIntExact(): Int = if (this in Int.MIN_VALUE..Int.MAX_VALUE) {
    toInt()
} else throw ArithmeticException("integer overflow")

@Throws(ArithmeticException::class)
actual infix fun Long.addExact(other: Long): Long {
    val result = this + other
    if ((this xor other).inv() and (this xor result) < 0) throw ArithmeticException("integer overflow")
    return result
}

@Throws(ArithmeticException::class)
actual infix fun Int.addExact(other: Int): Int = (this.toLong() + other.toLong()).toIntExact()

@Throws(ArithmeticException::class)
actual infix fun Long.subtractExact(other: Long): Long {
    val result = this - other
    if (result addExact other != this) throw ArithmeticException("integer overflow")
    return result
}

@Throws(ArithmeticException::class)
actual infix fun Int.subtractExact(other: Int): Int = (this.toLong() - other.toLong()).toIntExact()

@Throws(ArithmeticException::class)
actual infix fun Long.multiplyExact(other: Long): Long {
    val result = this * other
    val leadingZeros = this.countLeadingZeroBits() + this.inv().countLeadingZeroBits() +
        other.countLeadingZeroBits() + other.inv().countLeadingZeroBits()
    return when {
        leadingZeros > Long.SIZE_BITS + 1 -> result
        leadingZeros < Long.SIZE_BITS || this < 0L && other == Long.MIN_VALUE || this != 0L && result / this != other
        -> throw ArithmeticException("integer overflow")
        else -> result
    }
}

@Throws(ArithmeticException::class)
actual infix fun Int.multiplyExact(other: Int): Int = (this.toLong() * other.toLong()).toIntExact()
