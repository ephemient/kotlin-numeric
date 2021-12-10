package com.github.ephemient.kotlin.numeric

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.fail

class ConversionsTest {
    @Test
    fun testLongToULongExact() = testExact(
        Long::toULongExact, Long::toULongSaturating, ULong::toLong,
        *interestingLongs.toTypedArray(),
    )

    @Test
    fun testLongToULongSaturating() = testSaturating(
        Long::toULongSaturating, Long::toULongSaturating, ULong::toLong, { it > 0L },
        ULong.MIN_VALUE, ULong.MAX_VALUE, *interestingLongs.toTypedArray(),
    )

    @Test
    fun testULongToLongExact() = testExact(
        ULong::toLongExact, ULong::toLongSaturating, Long::toULong,
        *interestingULongs.toTypedArray(),
    )

    @Test
    fun testULongToLongSaturating() = testSaturating<ULong, Long>(
        ULong::toLongSaturating, ULong::toLongSaturating, Long::toULong, { it > 0UL },
        Long.MIN_VALUE, Long.MAX_VALUE, *interestingULongs.toTypedArray(),
    )

    @Test
    fun testLongToIntExact() = testExact(
        Long::toIntExact, Long::toInt, Int::toLong,
        *interestingLongs.toTypedArray(),
    )

    @Test
    fun testLongToIntSaturating() = testSaturating(
        Long::toIntSaturating, Long::toInt, Int::toLong, { it > 0L },
        Int.MIN_VALUE, Int.MAX_VALUE, *interestingLongs.toTypedArray(),
    )

    @Test
    fun testLongToUIntExact() = testExact(
        Long::toUIntExact, Long::toUInt, UInt::toLong,
        *interestingLongs.toTypedArray(),
    )

    @Test
    fun testLongToUIntSaturating() = testSaturating(
        Long::toUIntSaturating, Long::toUInt, UInt::toLong, { it > 0L },
        UInt.MIN_VALUE, UInt.MAX_VALUE, *interestingLongs.toTypedArray(),
    )

    @Test
    fun testULongToIntExact() = testExact(
        ULong::toIntExact, ULong::toIntSaturating, Int::toULong,
        *interestingULongs.toTypedArray(),
    )

    @Test
    fun testULongToIntSaturating() = testSaturating(
        ULong::toIntSaturating, ULong::toIntSaturating, Int::toULong, { it > 0UL },
        Int.MIN_VALUE, Int.MAX_VALUE, *interestingULongs.toTypedArray(),
    )

    @Test
    fun testULongToUIntExact() = testExact(
        ULong::toUIntExact, ULong::toUInt, UInt::toULong,
        *interestingULongs.toTypedArray(),
    )

    @Test
    fun testULongToUIntSaturating() = testSaturating(
        ULong::toUIntSaturating, ULong::toUInt, UInt::toULong, { it > 0UL },
        UInt.MIN_VALUE, UInt.MAX_VALUE, *interestingULongs.toTypedArray(),
    )

    @Test
    fun testLongToShortExact() = testExact(
        Long::toShortExact, Long::toShort, Short::toLong,
        *interestingLongs.toTypedArray(),
    )

    @Test
    fun testLongToShortSaturating() = testSaturating(
        Long::toShortSaturating, Long::toShort, Short::toLong, { it > 0L },
        Short.MIN_VALUE, Short.MAX_VALUE, *interestingLongs.toTypedArray(),
    )

    @Test
    fun testLongToUShortExact() = testExact(
        Long::toUShortExact, Long::toUShort, UShort::toLong,
        *interestingLongs.toTypedArray(),
    )

    @Test
    fun testLongToUShortSaturating() = testSaturating(
        Long::toUShortSaturating, Long::toUShort, UShort::toLong, { it > 0L },
        UShort.MIN_VALUE, UShort.MAX_VALUE, *interestingLongs.toTypedArray(),
    )

    @Test
    fun testULongToShortExact() = testExact(
        ULong::toShortExact, ULong::toShortSaturating, Short::toULong,
        *interestingULongs.toTypedArray(),
    )

    @Test
    fun testULongToShortSaturating() = testSaturating(
        ULong::toShortSaturating, ULong::toShortSaturating, Short::toULong, { it > 0UL },
        Short.MIN_VALUE, Short.MAX_VALUE, *interestingULongs.toTypedArray(),
    )

    @Test
    fun testULongToUShortExact() = testExact(
        ULong::toUShortExact, ULong::toUShort, UShort::toULong,
        *interestingULongs.toTypedArray(),
    )

    @Test
    fun testULongToUShortSaturating() = testSaturating(
        ULong::toUShortSaturating, ULong::toUShort, UShort::toULong, { it > 0UL },
        UShort.MIN_VALUE, UShort.MAX_VALUE, *interestingULongs.toTypedArray(),
    )

    @Test
    fun testLongToByteExact() = testExact(
        Long::toByteExact, Long::toByte, Byte::toLong,
        *interestingLongs.toTypedArray(),
    )

    @Test
    fun testLongToByteSaturating() = testSaturating(
        Long::toByteSaturating, Long::toByte, Byte::toLong, { it > 0L },
        Byte.MIN_VALUE, Byte.MAX_VALUE, *interestingLongs.toTypedArray(),
    )

    @Test
    fun testLongToUByteExact() = testExact(
        Long::toUByteExact, Long::toUByte, UByte::toLong,
        *interestingLongs.toTypedArray(),
    )

    @Test
    fun testLongToUByteSaturating() = testSaturating(
        Long::toUByteSaturating, Long::toUByte, UByte::toLong, { it > 0L },
        UByte.MIN_VALUE, UByte.MAX_VALUE, *interestingLongs.toTypedArray(),
    )

    @Test
    fun testULongToByteExact() = testExact(
        ULong::toByteExact, ULong::toByteSaturating, Byte::toULong,
        *interestingULongs.toTypedArray(),
    )

    @Test
    fun testULongToByteSaturating() = testSaturating(
        ULong::toByteSaturating, ULong::toByteSaturating, Byte::toULong, { it > 0UL },
        Byte.MIN_VALUE, Byte.MAX_VALUE, *interestingULongs.toTypedArray(),
    )

    @Test
    fun testULongToUByteExact() = testExact(
        ULong::toUByteExact, ULong::toUByte, UByte::toULong,
        *interestingULongs.toTypedArray(),
    )

    @Test
    fun testULongToUByteSaturating() = testSaturating(
        ULong::toUByteSaturating, ULong::toUByte, UByte::toULong, { it > 0UL },
        UByte.MIN_VALUE, UByte.MAX_VALUE, *interestingULongs.toTypedArray(),
    )

    @Test
    fun testIntToUIntExact() = testExact(
        Int::toUIntExact, Int::toUIntSaturating, UInt::toInt,
        *interestingInts.toTypedArray(),
    )

    @Test
    fun testIntToUIntSaturating() = testSaturating(
        Int::toUIntSaturating, Int::toUIntSaturating, UInt::toInt, { it > 0L },
        UInt.MIN_VALUE, UInt.MAX_VALUE, *interestingInts.toTypedArray(),
    )

    @Test
    fun testUIntToIntExact() = testExact(
        UInt::toIntExact, UInt::toIntSaturating, Int::toUInt,
        *interestingUInts.toTypedArray(),
    )

    @Test
    fun testUIntToIntSaturating() = testSaturating<UInt, Int>(
        UInt::toIntSaturating, UInt::toIntSaturating, Int::toUInt, { it > 0UL },
        Int.MIN_VALUE, Int.MAX_VALUE, *interestingUInts.toTypedArray(),
    )

    @Test
    fun testIntToShortExact() = testExact(
        Int::toShortExact, Int::toShort, Short::toInt,
        *interestingInts.toTypedArray(),
    )

    @Test
    fun testIntToShortSaturating() = testSaturating(
        Int::toShortSaturating, Int::toShort, Short::toInt, { it > 0L },
        Short.MIN_VALUE, Short.MAX_VALUE, *interestingInts.toTypedArray(),
    )

    @Test
    fun testIntToUShortExact() = testExact(
        Int::toUShortExact, Int::toUShort, UShort::toInt,
        *interestingInts.toTypedArray(),
    )

    @Test
    fun testIntToUShortSaturating() = testSaturating(
        Int::toUShortSaturating, Int::toUShort, UShort::toInt, { it > 0L },
        UShort.MIN_VALUE, UShort.MAX_VALUE, *interestingInts.toTypedArray(),
    )

    @Test
    fun testUIntToShortExact() = testExact(
        UInt::toShortExact, UInt::toShortSaturating, Short::toUInt,
        *interestingUInts.toTypedArray(),
    )

    @Test
    fun testUIntToShortSaturating() = testSaturating(
        UInt::toShortSaturating, UInt::toShortSaturating, Short::toUInt, { it > 0UL },
        Short.MIN_VALUE, Short.MAX_VALUE, *interestingUInts.toTypedArray(),
    )

    @Test
    fun testUIntToUShortExact() = testExact(
        UInt::toUShortExact, UInt::toUShort, UShort::toUInt,
        *interestingUInts.toTypedArray(),
    )

    @Test
    fun testUIntToUShortSaturating() = testSaturating(
        UInt::toUShortSaturating, UInt::toUShort, UShort::toUInt, { it > 0UL },
        UShort.MIN_VALUE, UShort.MAX_VALUE, *interestingUInts.toTypedArray(),
    )

    @Test
    fun testIntToByteExact() = testExact(
        Int::toByteExact, Int::toByte, Byte::toInt,
        *interestingInts.toTypedArray(),
    )

    @Test
    fun testIntToByteSaturating() = testSaturating(
        Int::toByteSaturating, Int::toByte, Byte::toInt, { it > 0L },
        Byte.MIN_VALUE, Byte.MAX_VALUE, *interestingInts.toTypedArray(),
    )

    @Test
    fun testIntToUByteExact() = testExact(
        Int::toUByteExact, Int::toUByte, UByte::toInt,
        *interestingInts.toTypedArray(),
    )

    @Test
    fun testIntToUByteSaturating() = testSaturating(
        Int::toUByteSaturating, Int::toUByte, UByte::toInt, { it > 0L },
        UByte.MIN_VALUE, UByte.MAX_VALUE, *interestingInts.toTypedArray(),
    )

    @Test
    fun testUIntToByteExact() = testExact(
        UInt::toByteExact, UInt::toByteSaturating, Byte::toUInt,
        *interestingUInts.toTypedArray(),
    )

    @Test
    fun testUIntToByteSaturating() = testSaturating(
        UInt::toByteSaturating, UInt::toByteSaturating, Byte::toUInt, { it > 0UL },
        Byte.MIN_VALUE, Byte.MAX_VALUE, *interestingUInts.toTypedArray(),
    )

    @Test
    fun testUIntToUByteExact() = testExact(
        UInt::toUByteExact, UInt::toUByte, UByte::toUInt,
        *interestingUInts.toTypedArray(),
    )

    @Test
    fun testUIntToUByteSaturating() = testSaturating(
        UInt::toUByteSaturating, UInt::toUByte, UByte::toUInt, { it > 0UL },
        UByte.MIN_VALUE, UByte.MAX_VALUE, *interestingUInts.toTypedArray(),
    )

    @Test
    fun testShortToUShortExact() = testExact(
        Short::toUShortExact, Short::toUShortSaturating, UShort::toShort,
        *interestingShorts.toTypedArray(),
    )

    @Test
    fun testShortToUShortSaturating() = testSaturating(
        Short::toUShortSaturating, Short::toUShortSaturating, UShort::toShort, { it > 0L },
        UShort.MIN_VALUE, UShort.MAX_VALUE, *interestingShorts.toTypedArray(),
    )

    @Test
    fun testUShortToShortExact() = testExact(
        UShort::toShortExact, UShort::toShortSaturating, Short::toUShort,
        *interestingUShorts.toTypedArray(),
    )

    @Test
    fun testUShortToShortSaturating() = testSaturating<UShort, Short>(
        UShort::toShortSaturating, UShort::toShortSaturating, Short::toUShort, { it > 0UL },
        Short.MIN_VALUE, Short.MAX_VALUE, *interestingUShorts.toTypedArray(),
    )

    @Test
    fun testShortToByteExact() = testExact(
        Short::toByteExact, Short::toByte, Byte::toShort,
        *interestingShorts.toTypedArray(),
    )

    @Test
    fun testShortToByteSaturating() = testSaturating(
        Short::toByteSaturating, Short::toByte, Byte::toShort, { it > 0L },
        Byte.MIN_VALUE, Byte.MAX_VALUE, *interestingShorts.toTypedArray(),
    )

    @Test
    fun testShortToUByteExact() = testExact(
        Short::toUByteExact, Short::toUByte, UByte::toShort,
        *interestingShorts.toTypedArray(),
    )

    @Test
    fun testShortToUByteSaturating() = testSaturating(
        Short::toUByteSaturating, Short::toUByte, UByte::toShort, { it > 0L },
        UByte.MIN_VALUE, UByte.MAX_VALUE, *interestingShorts.toTypedArray(),
    )

    @Test
    fun testUShortToByteExact() = testExact(
        UShort::toByteExact, UShort::toByteSaturating, Byte::toUShort,
        *interestingUShorts.toTypedArray(),
    )

    @Test
    fun testUShortToByteSaturating() = testSaturating(
        UShort::toByteSaturating, UShort::toByteSaturating, Byte::toUShort, { it > 0UL },
        Byte.MIN_VALUE, Byte.MAX_VALUE, *interestingUShorts.toTypedArray(),
    )

    @Test
    fun testUShortToUByteExact() = testExact(
        UShort::toUByteExact, UShort::toUByte, UByte::toUShort,
        *interestingUShorts.toTypedArray(),
    )

    @Test
    fun testUShortToUByteSaturating() = testSaturating(
        UShort::toUByteSaturating, UShort::toUByte, UByte::toUShort, { it > 0UL },
        UByte.MIN_VALUE, UByte.MAX_VALUE, *interestingUShorts.toTypedArray(),
    )

    @Test
    fun testByteToUByteExact() = testExact(
        Byte::toUByteExact, Byte::toUByteSaturating, UByte::toByte,
        *interestingBytes.toTypedArray(),
    )

    @Test
    fun testByteToUByteSaturating() = testSaturating(
        Byte::toUByteSaturating, Byte::toUByteSaturating, UByte::toByte, { it > 0L },
        UByte.MIN_VALUE, UByte.MAX_VALUE, *interestingBytes.toTypedArray(),
    )

    @Test
    fun testUByteToByteExact() = testExact(
        UByte::toByteExact, UByte::toByteSaturating, Byte::toUByte,
        *interestingUBytes.toTypedArray(),
    )

    @Test
    fun testUByteToByteSaturating() = testSaturating<UByte, Byte>(
        UByte::toByteSaturating, UByte::toByteSaturating, Byte::toUByte, { it > 0UL },
        Byte.MIN_VALUE, Byte.MAX_VALUE, *interestingUBytes.toTypedArray(),
    )

    private fun <T, U> testExact(
        toUExact: T.() -> U,
        toU: T.() -> U,
        toT: U.() -> T,
        vararg values: T
    ) {
        for (t in values) {
            val u = t.toU()
            if (t == u.toT()) {
                assertEquals(
                    u,
                    try {
                        t.toUExact()
                    } catch (e: ArithmeticException) {
                        fail(t.toString(), e)
                    },
                    t.toString(),
                )
            } else {
                assertFailsWith<ArithmeticException>(t.toString()) { t.toUExact() }
            }
        }
    }

    private fun <T, U> testSaturating(
        toUSaturating: T.() -> U,
        toU: T.() -> U,
        toT: U.() -> T,
        isPositive: (T) -> Boolean,
        minValue: U,
        maxValue: U,
        vararg values: T
    ) {
        for (t in values) {
            val u = toU(t)
            assertEquals(
                when {
                    t == u.toT() -> u
                    isPositive(t) -> maxValue
                    else -> minValue
                },
                t.toUSaturating(),
                t.toString(),
            )
        }
    }
}
