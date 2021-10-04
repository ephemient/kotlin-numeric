@file:JvmName("JvmMathKt")

package com.github.ephemient.kotlin.numeric

@Throws(ArithmeticException::class)
actual fun Long.toIntExact(): Int = Math.toIntExact(this)

@Throws(ArithmeticException::class)
actual infix fun Long.addExact(other: Long): Long = Math.addExact(this, other)

@Throws(ArithmeticException::class)
actual infix fun Int.addExact(other: Int): Int = Math.addExact(this, other)

@Throws(ArithmeticException::class)
actual infix fun Long.subtractExact(other: Long): Long = Math.subtractExact(this, other)

@Throws(ArithmeticException::class)
actual infix fun Int.subtractExact(other: Int): Int = Math.subtractExact(this, other)

@Throws(ArithmeticException::class)
actual infix fun Long.multiplyExact(other: Long): Long = Math.multiplyExact(this, other)

@Throws(ArithmeticException::class)
actual infix fun Int.multiplyExact(other: Int): Int = Math.multiplyExact(this, other)
