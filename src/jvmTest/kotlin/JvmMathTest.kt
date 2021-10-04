package com.github.ephemient.kotlin.numeric

import org.jetbrains.jetCheck.Generator
import org.jetbrains.jetCheck.Generator.constant
import org.jetbrains.jetCheck.Generator.from
import org.jetbrains.jetCheck.Generator.integers
import org.jetbrains.jetCheck.Generator.zipWith
import org.jetbrains.jetCheck.PropertyChecker.forAll
import kotlin.test.Test

class JvmMathTest {
    @Test
    fun testLongAddSaturating() {
        forAll(zipWith(longs(), longs(), ::Pair)) { (a, b) ->
            val c = a.toBigInteger() + b.toBigInteger()
            a addSaturating b == when {
                c < Long.MIN_VALUE.toBigInteger() -> Long.MIN_VALUE
                c > Long.MAX_VALUE.toBigInteger() -> Long.MAX_VALUE
                else -> c.toLong()
            }
        }
    }

    @Test
    fun testIntAddSaturating() {
        forAll(zipWith(integers(), integers(), ::Pair)) { (a, b) ->
            val c = a.toBigInteger() + b.toBigInteger()
            a addSaturating b == when {
                c < Int.MIN_VALUE.toBigInteger() -> Int.MIN_VALUE
                c > Int.MAX_VALUE.toBigInteger() -> Int.MAX_VALUE
                else -> c.toInt()
            }
        }
    }

    @Test
    fun testLongSubtractSaturating() {
        forAll(zipWith(longs(), longs(), ::Pair)) { (a, b) ->
            val c = a.toBigInteger() - b.toBigInteger()
            a subtractSaturating b == when {
                c < Long.MIN_VALUE.toBigInteger() -> Long.MIN_VALUE
                c > Long.MAX_VALUE.toBigInteger() -> Long.MAX_VALUE
                else -> c.toLong()
            }
        }
    }

    @Test
    fun testIntSubtractSaturating() {
        forAll(zipWith(integers(), integers(), ::Pair)) { (a, b) ->
            val c = a.toBigInteger() - b.toBigInteger()
            a subtractSaturating b == when {
                c < Int.MIN_VALUE.toBigInteger() -> Int.MIN_VALUE
                c > Int.MAX_VALUE.toBigInteger() -> Int.MAX_VALUE
                else -> c.toInt()
            }
        }
    }

    @Test
    fun testLongMultiplySaturating() {
        forAll(zipWith(longsGeometric(), longsGeometric(), ::Pair)) { (a, b) ->
            val c = a.toBigInteger() * b.toBigInteger()
            a multiplySaturating b == when {
                c < Long.MIN_VALUE.toBigInteger() -> Long.MIN_VALUE
                c > Long.MAX_VALUE.toBigInteger() -> Long.MAX_VALUE
                else -> c.toLong()
            }
        }
    }

    @Test
    fun testIntMultiplySaturating() {
        forAll(zipWith(intsGeometric(), intsGeometric(), ::Pair)) { (a, b) ->
            val c = a.toBigInteger() * b.toBigInteger()
            a multiplySaturating b == when {
                c < Int.MIN_VALUE.toBigInteger() -> Int.MIN_VALUE
                c > Int.MAX_VALUE.toBigInteger() -> Int.MAX_VALUE
                else -> c.toInt()
            }
        }
    }
}

private fun longs(): Generator<Long> = zipWith(integers(), integers()) { a, b ->
    a.toLong() shl 32 or b.toUInt().toLong()
}

private fun bits(n: Int): Generator<Int> = when {
    n <= 0 -> constant(0)
    n < Int.SIZE_BITS -> integers(0, (1 shl n) - 1)
    else -> integers()
}

private fun longsGeometric(): Generator<Long> = from { data ->
    val n = data.generate(integers(1, Long.SIZE_BITS))
    (data.generate(bits(n - Int.SIZE_BITS)).toLong() shl 32 or data.generate(bits(n)).toUInt().toLong()) - (1 shl n - 1)
}

private fun intsGeometric(): Generator<Int> = from { data ->
    val n = data.generate(integers(1, Int.SIZE_BITS))
    data.generate(bits(n)) - (1 shl n - 1)
}
