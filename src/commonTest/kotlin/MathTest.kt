package com.github.ephemient.kotlin.numeric

import kotlin.jvm.JvmName
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MathTest {
    @Test
    fun testLongAddExact(): Unit = forAllPairs(random::nextLong) { a, b ->
        val c = a.toBigInteger() + b.toBigInteger()
        when {
            c < Long.MIN_VALUE.toBigInteger() || c > Long.MAX_VALUE.toBigInteger() ->
                assertFailsWith<ArithmeticException>("$a + $b") { a addExact b }
            else -> assertEquals(c.toLong(), a addExact b, "$a + $b")
        }
    }

    @Test
    fun testULongAddExact(): Unit = forAllPairs(random::nextULong) { a, b ->
        val c = a.toBigInteger() + b.toBigInteger()
        when {
            c < ULong.MIN_VALUE.toBigInteger() || c > ULong.MAX_VALUE.toBigInteger() ->
                assertFailsWith<ArithmeticException>("$a + $b") { a addExact b }
            else -> assertEquals(c.toLong().toULong(), a addExact b, "$a + $b")
        }
    }

    @Test
    fun testIntAddExact(): Unit = forAllPairs(random::nextInt) { a, b ->
        val c = a.toBigInteger() + b.toBigInteger()
        when {
            c < Int.MIN_VALUE.toBigInteger() || c > Int.MAX_VALUE.toBigInteger() ->
                assertFailsWith<ArithmeticException>("$a + $b") { a addExact b }
            else -> assertEquals(c.toInt(), a addExact b, "$a + $b")
        }
    }

    @Test
    fun testUIntAddExact(): Unit = forAllPairs(random::nextUInt) { a, b ->
        val c = a.toBigInteger() + b.toBigInteger()
        when {
            c < UInt.MIN_VALUE.toBigInteger() || c > UInt.MAX_VALUE.toBigInteger() ->
                assertFailsWith<ArithmeticException>("$a + $b") { a addExact b }
            else -> assertEquals(c.toInt().toUInt(), a addExact b, "$a + $b")
        }
    }

    @Test
    fun testLongSubtractExact(): Unit = forAllPairs(random::nextLong) { a, b ->
        val c = a.toBigInteger() - b.toBigInteger()
        when {
            c < Long.MIN_VALUE.toBigInteger() || c > Long.MAX_VALUE.toBigInteger() ->
                assertFailsWith<ArithmeticException>("$a - $b") { a subtractExact b }
            else -> assertEquals(c.toLong(), a subtractExact b, "$a - $b")
        }
    }

    @Test
    fun testULongSubtractExact(): Unit = forAllPairs(random::nextULong) { a, b ->
        val c = a.toBigInteger() - b.toBigInteger()
        when {
            c < ULong.MIN_VALUE.toBigInteger() || c > ULong.MAX_VALUE.toBigInteger() ->
                assertFailsWith<ArithmeticException>("$a - $b") { a subtractExact b }
            else -> assertEquals(c.toLong().toULong(), a subtractExact b, "$a - $b")
        }
    }

    @Test
    fun testIntSubtractExact(): Unit = forAllPairs(random::nextInt) { a, b ->
        val c = a.toBigInteger() - b.toBigInteger()
        when {
            c < Int.MIN_VALUE.toBigInteger() || c > Int.MAX_VALUE.toBigInteger() ->
                assertFailsWith<ArithmeticException>("$a - $b") { a subtractExact b }
            else -> assertEquals(c.toInt(), a subtractExact b, "$a - $b")
        }
    }

    @Test
    fun testUIntSubtractExact(): Unit = forAllPairs(random::nextUInt) { a, b ->
        val c = a.toBigInteger() - b.toBigInteger()
        when {
            c < UInt.MIN_VALUE.toBigInteger() || c > UInt.MAX_VALUE.toBigInteger() ->
                assertFailsWith<ArithmeticException>("$a - $b") { a subtractExact b }
            else -> assertEquals(c.toInt().toUInt(), a subtractExact b, "$a - $b")
        }
    }

    @Test
    fun testLongMultiplyExact(): Unit = forAllPairs(random::nextLongGeometric) { a, b ->
        val c = a.toBigInteger() * b.toBigInteger()
        when {
            c < Long.MIN_VALUE.toBigInteger() || c > Long.MAX_VALUE.toBigInteger() ->
                assertFailsWith<ArithmeticException>("$a * $b") { a multiplyExact b }
            else -> assertEquals(c.toLong(), a multiplyExact b, "$a * $b")
        }
    }

    @Test
    fun testULongMultiplyExact(): Unit = forAllPairs(random::nextULongGeometric) { a, b ->
        val c = a.toBigInteger() * b.toBigInteger()
        when {
            c < ULong.MIN_VALUE.toBigInteger() || c > ULong.MAX_VALUE.toBigInteger() ->
                assertFailsWith<ArithmeticException>("$a * $b") { a multiplyExact b }
            else -> assertEquals(c.toLong().toULong(), a multiplyExact b, "$a * $b")
        }
    }

    @Test
    fun testIntMultiplyExact(): Unit = forAllPairs(random::nextIntGeometric) { a, b ->
        val c = a.toBigInteger() * b.toBigInteger()
        when {
            c < Int.MIN_VALUE.toBigInteger() || c > Int.MAX_VALUE.toBigInteger() ->
                assertFailsWith<ArithmeticException>("$a * $b") { a multiplyExact b }
            else -> assertEquals(c.toInt(), a multiplyExact b, "$a * $b")
        }
    }

    @Test
    fun testUIntMultiplyExact(): Unit = forAllPairs(random::nextUIntGeometric) { a, b ->
        val c = a.toBigInteger() * b.toBigInteger()
        when {
            c < UInt.MIN_VALUE.toBigInteger() || c > UInt.MAX_VALUE.toBigInteger() ->
                assertFailsWith<ArithmeticException>("$a * $b") { a multiplyExact b }
            else -> assertEquals(c.toInt().toUInt(), a multiplyExact b, "$a * $b")
        }
    }

    @Test
    fun testLongDivideExact(): Unit = forAllPairs(random::nextLongGeometric) { a, b ->
        if (b == 0L) {
            assertFailsWith<ArithmeticException>("$a / $b") { a divideExact b }
        } else {
            val c = a.toBigInteger() / b.toBigInteger()
            when {
                c < Long.MIN_VALUE.toBigInteger() || c > Long.MAX_VALUE.toBigInteger() ->
                    assertFailsWith<ArithmeticException>("$a * $b") { a divideExact b }
                else -> assertEquals(c.toLong(), a divideExact b, "$a / $b")
            }
        }
    }

    @Test
    fun testIntDivideExact(): Unit = forAllPairs(random::nextIntGeometric) { a, b ->
        if (b == 0) {
            assertFailsWith<ArithmeticException>("$a / $b") { a divideExact b }
        } else {
            val c = a.toBigInteger() / b.toBigInteger()
            when {
                c < Int.MIN_VALUE.toBigInteger() || c > Int.MAX_VALUE.toBigInteger() ->
                    assertFailsWith<ArithmeticException>("$a * $b") { a divideExact b }
                else -> assertEquals(c.toInt(), a divideExact b, "$a / $b")
            }
        }
    }

    @Test
    fun testLongAddSaturating(): Unit = forAllPairs(random::nextLong) { a, b ->
        val c = a.toBigInteger() + b.toBigInteger()
        assertEquals(
            when {
                c < Long.MIN_VALUE.toBigInteger() -> Long.MIN_VALUE
                c > Long.MAX_VALUE.toBigInteger() -> Long.MAX_VALUE
                else -> c.toLong()
            },
            a addSaturating b, "$a + $b"
        )
    }

    @Test
    fun testULongAddSaturating(): Unit = forAllPairs(random::nextULong) { a, b ->
        val c = a.toBigInteger() + b.toBigInteger()
        assertEquals(
            when {
                c < ULong.MIN_VALUE.toBigInteger() -> ULong.MIN_VALUE
                c > ULong.MAX_VALUE.toBigInteger() -> ULong.MAX_VALUE
                else -> c.toLong().toULong()
            },
            a addSaturating b, "$a + $b"
        )
    }

    @Test
    fun testIntAddSaturating(): Unit = forAllPairs(random::nextInt) { a, b ->
        val c = a.toBigInteger() + b.toBigInteger()
        assertEquals(
            when {
                c < Int.MIN_VALUE.toBigInteger() -> Int.MIN_VALUE
                c > Int.MAX_VALUE.toBigInteger() -> Int.MAX_VALUE
                else -> c.toInt()
            },
            a addSaturating b, "$a + $b"
        )
    }

    @Test
    fun testUIntAddSaturating(): Unit = forAllPairs(random::nextUInt) { a, b ->
        val c = a.toBigInteger() + b.toBigInteger()
        assertEquals(
            when {
                c < UInt.MIN_VALUE.toBigInteger() -> UInt.MIN_VALUE
                c > UInt.MAX_VALUE.toBigInteger() -> UInt.MAX_VALUE
                else -> c.toInt().toUInt()
            },
            a addSaturating b, "$a + $b"
        )
    }

    @Test
    fun testLongSubtractSaturating(): Unit = forAllPairs(random::nextLong) { a, b ->
        val c = a.toBigInteger() - b.toBigInteger()
        assertEquals(
            when {
                c < Long.MIN_VALUE.toBigInteger() -> Long.MIN_VALUE
                c > Long.MAX_VALUE.toBigInteger() -> Long.MAX_VALUE
                else -> c.toLong()
            },
            a subtractSaturating b, "$a - $b"
        )
    }

    @Test
    fun testULongSubtractSaturating(): Unit = forAllPairs(random::nextULong) { a, b ->
        val c = a.toBigInteger() - b.toBigInteger()
        assertEquals(
            when {
                c < ULong.MIN_VALUE.toBigInteger() -> ULong.MIN_VALUE
                c > ULong.MAX_VALUE.toBigInteger() -> ULong.MAX_VALUE
                else -> c.toLong().toULong()
            },
            a subtractSaturating b, "$a - $b"
        )
    }

    @Test
    fun testIntSubtractSaturating(): Unit = forAllPairs(random::nextInt) { a, b ->
        val c = a.toBigInteger() - b.toBigInteger()
        assertEquals(
            when {
                c < Int.MIN_VALUE.toBigInteger() -> Int.MIN_VALUE
                c > Int.MAX_VALUE.toBigInteger() -> Int.MAX_VALUE
                else -> c.toInt()
            },
            a subtractSaturating b, "$a - $b"
        )
    }

    @Test
    fun testUIntSubtractSaturating(): Unit = forAllPairs(random::nextUInt) { a, b ->
        val c = a.toBigInteger() - b.toBigInteger()
        assertEquals(
            when {
                c < UInt.MIN_VALUE.toBigInteger() -> UInt.MIN_VALUE
                c > UInt.MAX_VALUE.toBigInteger() -> UInt.MAX_VALUE
                else -> c.toInt().toUInt()
            },
            a subtractSaturating b, "$a - $b"
        )
    }

    @Test
    fun testLongMultiplySaturating(): Unit = forAllPairs(random::nextLongGeometric) { a, b ->
        val c = a.toBigInteger() * b.toBigInteger()
        assertEquals(
            when {
                c < Long.MIN_VALUE.toBigInteger() -> Long.MIN_VALUE
                c > Long.MAX_VALUE.toBigInteger() -> Long.MAX_VALUE
                else -> c.toLong()
            },
            a multiplySaturating b, "$a * $b"
        )
    }

    @Test
    fun testULongMultiplySaturating(): Unit = forAllPairs(random::nextULongGeometric) { a, b ->
        val c = a.toBigInteger() * b.toBigInteger()
        assertEquals(
            when {
                c < ULong.MIN_VALUE.toBigInteger() -> ULong.MIN_VALUE
                c > ULong.MAX_VALUE.toBigInteger() -> ULong.MAX_VALUE
                else -> c.toLong().toULong()
            },
            a multiplySaturating b, "$a * $b | ${a.toBigInteger()} * ${b.toBigInteger()} = $c"
        )
    }

    @Test
    fun testIntMultiplySaturating(): Unit = forAllPairs(random::nextIntGeometric) { a, b ->
        val c = a.toBigInteger() * b.toBigInteger()
        assertEquals(
            when {
                c < Int.MIN_VALUE.toBigInteger() -> Int.MIN_VALUE
                c > Int.MAX_VALUE.toBigInteger() -> Int.MAX_VALUE
                else -> c.toInt()
            },
            a multiplySaturating b, "$a * $b"
        )
    }

    @Test
    fun testUIntMultiplySaturating(): Unit = forAllPairs(random::nextUIntGeometric) { a, b ->
        val c = a.toBigInteger() * b.toBigInteger()
        assertEquals(
            when {
                c < UInt.MIN_VALUE.toBigInteger() -> UInt.MIN_VALUE
                c > UInt.MAX_VALUE.toBigInteger() -> UInt.MAX_VALUE
                else -> c.toInt().toUInt()
            },
            a multiplySaturating b, "$a * $b"
        )
    }

    @Test
    fun testLongDivideSaturating(): Unit = forAllPairs(random::nextLongGeometric) { a, b ->
        if (b == 0L) {
            assertFailsWith<ArithmeticException>("$a / $b") { a divideSaturating b }
        } else {
            val c = a.toBigInteger() / b.toBigInteger()
            assertEquals(
                when {
                    c < Long.MIN_VALUE.toBigInteger() -> Long.MIN_VALUE
                    c > Long.MAX_VALUE.toBigInteger() -> Long.MAX_VALUE
                    else -> c.toLong()
                },
                a divideSaturating b, "$a / $b"
            )
        }
    }

    @Test
    fun testIntDivideSaturating(): Unit = forAllPairs(random::nextIntGeometric) { a, b ->
        if (b == 0) {
            assertFailsWith<ArithmeticException>("$a / $b") { a divideSaturating b }
        } else {
            val c = a.toBigInteger() / b.toBigInteger()
            assertEquals(
                when {
                    c < Int.MIN_VALUE.toBigInteger() -> Int.MIN_VALUE
                    c > Int.MAX_VALUE.toBigInteger() -> Int.MAX_VALUE
                    else -> c.toInt()
                },
                a divideSaturating b, "$a / $b"
            )
        }
    }

    private val random = Random(MathTest::class.hashCode())
}

@JvmName("forAllPairsLong")
private fun forAllPairs(next: () -> Long, test: (Long, Long) -> Unit) {
    for (x in interestingLongs) for (y in interestingLongs) test(x, y)
    repeat(10) { for (x in interestingLongs) test(x, next()) }
    repeat(10) { for (y in interestingLongs) test(next(), y) }
    repeat(100) { test(next(), next()) }
}

@JvmName("forAllPairsULong")
private fun forAllPairs(next: () -> ULong, test: (ULong, ULong) -> Unit) {
    for (x in interestingULongs) for (y in interestingULongs) test(x, y)
    repeat(10) { for (x in interestingULongs) test(x, next()) }
    repeat(10) { for (y in interestingULongs) test(next(), y) }
    repeat(100) { test(next(), next()) }
}

@JvmName("forAllPairsInt")
private fun forAllPairs(next: () -> Int, test: (Int, Int) -> Unit) {
    for (x in interestingInts) for (y in interestingInts) test(x, y)
    repeat(10) { for (x in interestingInts) test(x, next()) }
    repeat(10) { for (y in interestingInts) test(next(), y) }
    repeat(100) { test(next(), next()) }
}

@JvmName("forAllPairsUInt")
private fun forAllPairs(next: () -> UInt, test: (UInt, UInt) -> Unit) {
    for (x in interestingUInts) for (y in interestingUInts) test(x, y)
    repeat(10) { for (x in interestingUInts) test(x, next()) }
    repeat(10) { for (y in interestingUInts) test(next(), y) }
    repeat(100) { test(next(), next()) }
}
