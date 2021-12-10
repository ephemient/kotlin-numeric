@file:JvmName("JvmMathKt")
@file:Suppress("NOTHING_TO_INLINE")

package com.github.ephemient.kotlin.numeric

@Throws(ArithmeticException::class)
actual inline fun Long.toIntExact(): Int = Math.toIntExact(this)

@Throws(ArithmeticException::class)
actual inline infix fun Long.addExact(other: Long): Long = Math.addExact(this, other)

@Throws(ArithmeticException::class)
actual inline infix fun Int.addExact(other: Int): Int = Math.addExact(this, other)

@Throws(ArithmeticException::class)
actual inline infix fun Long.subtractExact(other: Long): Long = Math.subtractExact(this, other)

@Throws(ArithmeticException::class)
actual inline infix fun Int.subtractExact(other: Int): Int = Math.subtractExact(this, other)

@Throws(ArithmeticException::class)
actual inline infix fun Long.multiplyExact(other: Long): Long = Math.multiplyExact(this, other)

@Throws(ArithmeticException::class)
actual inline infix fun Int.multiplyExact(other: Int): Int = Math.multiplyExact(this, other)

@Throws(ArithmeticException::class)
actual inline infix fun Long.divideExact(other: Long): Long =
    if (this == Long.MIN_VALUE && other == -1L) throw ArithmeticException("integer overflow")
    else this / other

@Throws(ArithmeticException::class)
actual inline infix fun Int.divideExact(other: Int): Int =
    if (this == Int.MIN_VALUE && other == -1) throw ArithmeticException("integer overflow")
    else this / other

@Throws(ArithmeticException::class)
actual inline infix fun Long.divideSaturating(other: Long): Long =
    if (this == Long.MIN_VALUE && other == -1L) Long.MAX_VALUE
    else this / other

@Throws(ArithmeticException::class)
actual inline infix fun Int.divideSaturating(other: Int): Int =
    if (this == Int.MIN_VALUE && other == -1) Int.MAX_VALUE
    else this / other
