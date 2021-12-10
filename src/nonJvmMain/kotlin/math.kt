@file:Suppress("NOTHING_TO_INLINE")

package com.github.ephemient.kotlin.numeric

@Throws(ArithmeticException::class)
actual inline fun Long.toIntExact(): Int = if (this in Int.MIN_VALUE..Int.MAX_VALUE) {
    toInt()
} else throw ArithmeticException("integer overflow")

@Throws(ArithmeticException::class)
actual inline infix fun Long.addExact(other: Long): Long {
    val result = this + other
    if ((this xor other).inv() and (this xor result) < 0) throw ArithmeticException("integer overflow")
    return result
}

@Throws(ArithmeticException::class)
actual inline infix fun Int.addExact(other: Int): Int = (this.toLong() + other.toLong()).toIntExact()

@Throws(ArithmeticException::class)
actual inline infix fun Long.subtractExact(other: Long): Long {
    val result = this - other
    if (result addExact other != this) throw ArithmeticException("integer overflow")
    return result
}

@Throws(ArithmeticException::class)
actual inline infix fun Int.subtractExact(other: Int): Int = (this.toLong() - other.toLong()).toIntExact()

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
actual inline infix fun Int.multiplyExact(other: Int): Int = (this.toLong() * other.toLong()).toIntExact()

@Throws(ArithmeticException::class)
actual inline infix fun Long.divideExact(other: Long): Long = when {
    other == 0L -> throw ArithmeticException("/ by zero")
    this == Long.MIN_VALUE && other == -1L -> throw ArithmeticException("integer overflow")
    else -> this / other
}

@Throws(ArithmeticException::class)
actual inline infix fun Int.divideExact(other: Int): Int = when {
    other == 0 -> throw ArithmeticException("/ by zero")
    this == Int.MIN_VALUE && other == -1 -> throw ArithmeticException("integer overflow")
    else -> this / other
}

@Throws(ArithmeticException::class)
actual inline infix fun Long.divideSaturating(other: Long): Long = when {
    other == 0L -> throw ArithmeticException("/ by zero")
    this == Long.MIN_VALUE && other == -1L -> Long.MAX_VALUE
    else -> this / other
}

@Throws(ArithmeticException::class)
actual inline infix fun Int.divideSaturating(other: Int): Int = when {
    other == 0 -> throw ArithmeticException("/ by zero")
    this == Int.MIN_VALUE && other == -1 -> Int.MAX_VALUE
    else -> this / other
}
