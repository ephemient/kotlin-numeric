@file:Suppress("TooManyFunctions")

package com.github.ephemient.kotlin.numeric

expect class BigInteger : Number, Comparable<BigInteger> {
    fun signum(): Int

    fun abs(): BigInteger

    fun negate(): BigInteger

    fun add(augend: BigInteger): BigInteger

    fun subtract(subtrahend: BigInteger): BigInteger

    fun multiply(multiplicand: BigInteger): BigInteger

    @Throws(ArithmeticException::class)
    fun divideAndRemainder(divisor: BigInteger): Array<BigInteger>

    @Throws(ArithmeticException::class)
    fun divide(divisor: BigInteger): BigInteger

    @Throws(ArithmeticException::class)
    fun remainder(divisor: BigInteger): BigInteger

    @Throws(ArithmeticException::class)
    fun pow(exponent: Int): BigInteger

    fun toString(radix: Int): String
}

expect object BigIntegers {
    val ZERO: BigInteger
    val ONE: BigInteger
    val TEN: BigInteger
}

@Throws(NumberFormatException::class)
expect fun String.toBigInteger(radix: Int = 10): BigInteger

expect fun Long.toBigInteger(): BigInteger

expect fun ULong.toBigInteger(): BigInteger

expect fun Int.toBigInteger(): BigInteger

expect fun UInt.toBigInteger(): BigInteger

operator fun BigInteger.unaryMinus(): BigInteger = negate()

operator fun BigInteger.plus(augend: BigInteger): BigInteger = add(augend)

operator fun BigInteger.minus(subtrahend: BigInteger): BigInteger = subtract(subtrahend)

operator fun BigInteger.times(multiplicand: BigInteger): BigInteger = multiply(multiplicand)

@Throws(ArithmeticException::class)
operator fun BigInteger.div(divisor: BigInteger): BigInteger = divide(divisor)

@Throws(ArithmeticException::class)
operator fun BigInteger.rem(divisor: BigInteger): BigInteger = remainder(divisor)
