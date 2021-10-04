package com.github.ephemient.kotlin.numeric

import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalStdlibApi
class BigIntegerTest {
    @Test
    fun testAbs() {
        for (x in randomLongs) {
            when (val expected = abs(x)) {
                Long.MIN_VALUE -> continue
                else -> assertEquals(expected.toBigInteger(), x.toBigInteger().abs(), "$x.abs()")
            }
        }
    }

    @Test
    fun testNegate() {
        for (x in randomLongs) {
            when (val expected = -x) {
                Long.MIN_VALUE -> continue
                else -> assertEquals(expected.toBigInteger(), -x.toBigInteger(), "-$x")
            }
        }
    }

    @Test
    fun testAdd() {
        for (x in randomLongs) {
            for (y in randomLongs) {
                when (val expected = x addSaturating y) {
                    Long.MIN_VALUE, Long.MAX_VALUE -> continue
                    else -> assertEquals(expected.toBigInteger(), x.toBigInteger() + y.toBigInteger(), "$x + $y")
                }
            }
        }
    }

    @Test
    fun testSubtract() {
        for (x in randomLongs) {
            for (y in randomLongs) {
                when (val expected = x subtractSaturating y) {
                    Long.MIN_VALUE, Long.MAX_VALUE -> {}
                    else -> assertEquals(expected.toBigInteger(), x.toBigInteger() - y.toBigInteger(), "$x - $y")
                }
            }
        }
    }

    @Test
    fun testMultiply() {
        for (x in randomLongs) {
            for (y in randomLongs) {
                when (val expected = x multiplySaturating y) {
                    Long.MIN_VALUE -> {}
                    Long.MAX_VALUE -> {}
                    else -> assertEquals(expected.toBigInteger(), x.toBigInteger() * y.toBigInteger(), "$x * $y")
                }
            }
        }
    }

    @Test
    fun testDivideAndRemainder() {
        for (x in randomLongs) {
            if (x == 0L) continue
            for (y in randomLongs) {
                val expected = y / x to y % x
                val (div, rem) = y.toBigInteger().divideAndRemainder(x.toBigInteger())
                assertEquals(expected, div.toLong() to rem.toLong(), "$y / $x")
            }
        }
    }

    @Test
    fun testComparable() {
        for (x in randomLongs) {
            val a = x.toBigInteger()
            for (y in randomLongs) {
                val b = y.toBigInteger()
                assertEquals(x == y, a == b, "$x == $y")
                assertEquals(x < y, a < b, "$x < $y")
                assertEquals(x > y, a > b, "$x > $y")
            }
        }
    }

    @Test
    fun testHashCode() {
        val hashes = randomLongs.groupBy { it.toBigInteger().hashCode() }
        assertTrue("${hashes.size} ≥ ${sqrt(randomLongs.size.toDouble()).toInt()}") {
            hashes.size >= sqrt(randomLongs.size.toDouble()).toInt()
        }
        for ((hash, values) in hashes) {
            for (value in values) {
                assertEquals(hash, value.toBigInteger().hashCode(), "($value).hashCode()")
            }
        }
    }

    @Test
    fun testToString() {
        for (x in randomLongs) {
            val y = x.toBigInteger()
            for (radix in 2..35) { // radix=36 broken: https://youtrack.jetbrains.com/issue/KT-48924
                val s = x.toString(radix = radix)
                assertEquals(s, y.toString(radix), "($x).toString(radix=$radix)")
                assertEquals(y, s.toBigInteger(radix), "\"$s\".toBigInteger(radix=$radix)")
            }
        }
    }
}

@ExperimentalStdlibApi
private val randomLongs by lazy {
    val random = Random(BigIntegerTest::class.hashCode())
    interestingLongs.toMutableSet().apply {
        while (size < 100) {
            val n = random.nextInt(Long.SIZE_BITS) + 1
            add((random.nextBits(n - Int.SIZE_BITS).toLong() shl 32 or random.nextBits(n).toLong()) - (1L shl n - 1))
        }
    }
}
