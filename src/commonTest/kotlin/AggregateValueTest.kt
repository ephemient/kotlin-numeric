package com.github.ephemient.kotlin.numeric

import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.test.Test
import kotlin.test.assertEquals

class AggregateValueTest {
    @Test
    fun testIntPair(): Unit = testAggregateValue(
        constructor = { values ->
            val first by values
            val second by values
            IntPair(first = first, second = second)
        },
        getters = mapOf(
            "first" to listOf(IntPair::first, IntPair::component1),
            "second" to listOf(IntPair::second, IntPair::component2),
        ),
        copy = { value, values ->
            val first by values
            val second by values
            when (values.keys) {
                emptySet<String>() -> value.copy()
                setOf("first") -> value.copy(first = first)
                setOf("second") -> value.copy(second = second)
                setOf("first", "second") -> value.copy(first = first, second = second)
                else -> throw IllegalStateException("Check failed.")
            }
        },
        next = random::nextInt,
    )

    @Test
    fun testUIntPair(): Unit = testAggregateValue(
        constructor = { values ->
            val first by values
            val second by values
            UIntPair(first = first, second = second)
        },
        getters = mapOf(
            "first" to listOf(UIntPair::first, UIntPair::component1),
            "second" to listOf(UIntPair::second, UIntPair::component2),
        ),
        copy = { value, values ->
            val first by values
            val second by values
            when (values.keys) {
                emptySet<String>() -> value.copy()
                setOf("first") -> value.copy(first = first)
                setOf("second") -> value.copy(second = second)
                setOf("first", "second") -> value.copy(first = first, second = second)
                else -> throw IllegalStateException("Check failed.")
            }
        },
        next = random::nextUInt,
    )

    @Test
    fun testFloatPair(): Unit = testAggregateValue(
        constructor = { values ->
            val first by values
            val second by values
            FloatPair(first = first, second = second)
        },
        getters = mapOf(
            "first" to listOf(FloatPair::first, FloatPair::component1),
            "second" to listOf(FloatPair::second, FloatPair::component2),
        ),
        copy = { value, values ->
            val first by values
            val second by values
            when (values.keys) {
                emptySet<String>() -> value.copy()
                setOf("first") -> value.copy(first = first)
                setOf("second") -> value.copy(second = second)
                setOf("first", "second") -> value.copy(first = first, second = second)
                else -> throw IllegalStateException("Check failed.")
            }
        },
        next = {
            var float: Float
            do {
                float = Float.fromBits(random.nextInt())
            } while (float.isNaN())
            float
        },
    )

    @Test
    fun testShortPair(): Unit = testAggregateValue(
        constructor = { values ->
            val first by values
            val second by values
            ShortPair(first = first, second = second)
        },
        getters = mapOf(
            "first" to listOf(ShortPair::first, ShortPair::component1),
            "second" to listOf(ShortPair::second, ShortPair::component2),
        ),
        copy = { value, values ->
            val first by values
            val second by values
            when (values.keys) {
                emptySet<String>() -> value.copy()
                setOf("first") -> value.copy(first = first)
                setOf("second") -> value.copy(second = second)
                setOf("first", "second") -> value.copy(first = first, second = second)
                else -> throw IllegalStateException("Check failed.")
            }
        },
        next = random::nextShort,
    )

    @Test
    fun testUShortPair(): Unit = testAggregateValue(
        constructor = { values ->
            val first by values
            val second by values
            UShortPair(first = first, second = second)
        },
        getters = mapOf(
            "first" to listOf(UShortPair::first, UShortPair::component1),
            "second" to listOf(UShortPair::second, UShortPair::component2),
        ),
        copy = { value, values ->
            val first by values
            val second by values
            when (values.keys) {
                emptySet<String>() -> value.copy()
                setOf("first") -> value.copy(first = first)
                setOf("second") -> value.copy(second = second)
                setOf("first", "second") -> value.copy(first = first, second = second)
                else -> throw IllegalStateException("Check failed.")
            }
        },
        next = random::nextUShort,
    )

    @Test
    fun testShortQuad(): Unit = testAggregateValue(
        constructor = { values ->
            val first by values
            val second by values
            val third by values
            val fourth by values
            ShortQuad(first = first, second = second, third = third, fourth = fourth)
        },
        getters = mapOf(
            "first" to listOf(ShortQuad::first, ShortQuad::component1),
            "second" to listOf(ShortQuad::second, ShortQuad::component2),
            "third" to listOf(ShortQuad::third, ShortQuad::component3),
            "fourth" to listOf(ShortQuad::fourth, ShortQuad::component4),
        ),
        copy = { value, values ->
            val first by values
            val second by values
            val third by values
            val fourth by values
            when (values.keys) {
                emptySet<String>() -> value.copy()
                setOf("first") -> value.copy(first = first)
                setOf("second") -> value.copy(second = second)
                setOf("third") -> value.copy(third = third)
                setOf("fourth") -> value.copy(fourth = fourth)
                setOf("first", "second") -> value.copy(first = first, second = second)
                setOf("first", "third") -> value.copy(first = first, third = third)
                setOf("first", "fourth") -> value.copy(first = first, fourth = fourth)
                setOf("second", "third") -> value.copy(second = second, third = third)
                setOf("second", "fourth") -> value.copy(second = second, fourth = fourth)
                setOf("third", "fourth") -> value.copy(third = third, fourth = fourth)
                setOf("first", "second", "third") -> value.copy(first = first, second = second, third = third)
                setOf("first", "second", "fourth") -> value.copy(first = first, second = second, fourth = fourth)
                setOf("first", "third", "fourth") -> value.copy(first = first, third = third, fourth = fourth)
                setOf("second", "third", "fourth") -> value.copy(second = second, third = third, fourth = fourth)
                setOf("first", "second", "third", "fourth") ->
                    value.copy(first = first, second = second, third = third, fourth = fourth)
                else -> throw IllegalStateException("Check failed.")
            }
        },
        next = random::nextShort,
    )

    @Test
    fun testUShortQuad(): Unit = testAggregateValue(
        constructor = { values ->
            val first by values
            val second by values
            val third by values
            val fourth by values
            UShortQuad(first = first, second = second, third = third, fourth = fourth)
        },
        getters = mapOf(
            "first" to listOf(UShortQuad::first, UShortQuad::component1),
            "second" to listOf(UShortQuad::second, UShortQuad::component2),
            "third" to listOf(UShortQuad::third, UShortQuad::component3),
            "fourth" to listOf(UShortQuad::fourth, UShortQuad::component4),
        ),
        copy = { value, values ->
            val first by values
            val second by values
            val third by values
            val fourth by values
            when (values.keys) {
                emptySet<String>() -> value.copy()
                setOf("first") -> value.copy(first = first)
                setOf("second") -> value.copy(second = second)
                setOf("third") -> value.copy(third = third)
                setOf("fourth") -> value.copy(fourth = fourth)
                setOf("first", "second") -> value.copy(first = first, second = second)
                setOf("first", "third") -> value.copy(first = first, third = third)
                setOf("first", "fourth") -> value.copy(first = first, fourth = fourth)
                setOf("second", "third") -> value.copy(second = second, third = third)
                setOf("second", "fourth") -> value.copy(second = second, fourth = fourth)
                setOf("third", "fourth") -> value.copy(third = third, fourth = fourth)
                setOf("first", "second", "third") -> value.copy(first = first, second = second, third = third)
                setOf("first", "second", "fourth") -> value.copy(first = first, second = second, fourth = fourth)
                setOf("first", "third", "fourth") -> value.copy(first = first, third = third, fourth = fourth)
                setOf("second", "third", "fourth") -> value.copy(second = second, third = third, fourth = fourth)
                setOf("first", "second", "third", "fourth") ->
                    value.copy(first = first, second = second, third = third, fourth = fourth)
                else -> throw IllegalStateException("Check failed.")
            }
        },
        next = random::nextUShort,
    )

    @Test
    fun testByteQuad(): Unit = testAggregateValue(
        constructor = { values ->
            val first by values
            val second by values
            val third by values
            val fourth by values
            ByteQuad(first = first, second = second, third = third, fourth = fourth)
        },
        getters = mapOf(
            "first" to listOf(ByteQuad::first, ByteQuad::component1),
            "second" to listOf(ByteQuad::second, ByteQuad::component2),
            "third" to listOf(ByteQuad::third, ByteQuad::component3),
            "fourth" to listOf(ByteQuad::fourth, ByteQuad::component4),
        ),
        copy = { value, values ->
            val first by values
            val second by values
            val third by values
            val fourth by values
            when (values.keys) {
                emptySet<String>() -> value.copy()
                setOf("first") -> value.copy(first = first)
                setOf("second") -> value.copy(second = second)
                setOf("third") -> value.copy(third = third)
                setOf("fourth") -> value.copy(fourth = fourth)
                setOf("first", "second") -> value.copy(first = first, second = second)
                setOf("first", "third") -> value.copy(first = first, third = third)
                setOf("first", "fourth") -> value.copy(first = first, fourth = fourth)
                setOf("second", "third") -> value.copy(second = second, third = third)
                setOf("second", "fourth") -> value.copy(second = second, fourth = fourth)
                setOf("third", "fourth") -> value.copy(third = third, fourth = fourth)
                setOf("first", "second", "third") -> value.copy(first = first, second = second, third = third)
                setOf("first", "second", "fourth") -> value.copy(first = first, second = second, fourth = fourth)
                setOf("first", "third", "fourth") -> value.copy(first = first, third = third, fourth = fourth)
                setOf("second", "third", "fourth") -> value.copy(second = second, third = third, fourth = fourth)
                setOf("first", "second", "third", "fourth") ->
                    value.copy(first = first, second = second, third = third, fourth = fourth)
                else -> throw IllegalStateException("Check failed.")
            }
        },
        next = random::nextByte,
    )

    @Test
    fun testUByteQuad(): Unit = testAggregateValue(
        constructor = { values ->
            val first by values
            val second by values
            val third by values
            val fourth by values
            UByteQuad(first = first, second = second, third = third, fourth = fourth)
        },
        getters = mapOf(
            "first" to listOf(UByteQuad::first, UByteQuad::component1),
            "second" to listOf(UByteQuad::second, UByteQuad::component2),
            "third" to listOf(UByteQuad::third, UByteQuad::component3),
            "fourth" to listOf(UByteQuad::fourth, UByteQuad::component4),
        ),
        copy = { value, values ->
            val first by values
            val second by values
            val third by values
            val fourth by values
            when (values.keys) {
                emptySet<String>() -> value.copy()
                setOf("first") -> value.copy(first = first)
                setOf("second") -> value.copy(second = second)
                setOf("third") -> value.copy(third = third)
                setOf("fourth") -> value.copy(fourth = fourth)
                setOf("first", "second") -> value.copy(first = first, second = second)
                setOf("first", "third") -> value.copy(first = first, third = third)
                setOf("first", "fourth") -> value.copy(first = first, fourth = fourth)
                setOf("second", "third") -> value.copy(second = second, third = third)
                setOf("second", "fourth") -> value.copy(second = second, fourth = fourth)
                setOf("third", "fourth") -> value.copy(third = third, fourth = fourth)
                setOf("first", "second", "third") -> value.copy(first = first, second = second, third = third)
                setOf("first", "second", "fourth") -> value.copy(first = first, second = second, fourth = fourth)
                setOf("first", "third", "fourth") -> value.copy(first = first, third = third, fourth = fourth)
                setOf("second", "third", "fourth") -> value.copy(second = second, third = third, fourth = fourth)
                setOf("first", "second", "third", "fourth") ->
                    value.copy(first = first, second = second, third = third, fourth = fourth)
                else -> throw IllegalStateException("Check failed.")
            }
        },
        next = random::nextUByte,
    )

    @Test
    fun testByteOct(): Unit = testAggregateValue(
        constructor = { values ->
            val first by values
            val second by values
            val third by values
            val fourth by values
            val fifth by values
            val sixth by values
            val seventh by values
            val eighth by values
            ByteOct(first, second, third, fourth, fifth, sixth, seventh, eighth)
        },
        getters = mapOf(
            "first" to listOf(ByteOct::first, ByteOct::component1),
            "second" to listOf(ByteOct::second, ByteOct::component2),
            "third" to listOf(ByteOct::third, ByteOct::component3),
            "fourth" to listOf(ByteOct::fourth, ByteOct::component4),
            "fifth" to listOf(ByteOct::fifth, ByteOct::component5),
            "sixth" to listOf(ByteOct::sixth, ByteOct::component6),
            "seventh" to listOf(ByteOct::seventh, ByteOct::component7),
            "eighth" to listOf(ByteOct::eighth, ByteOct::component8),
        ),
        copy = { value, values ->
            values.entries.fold(value) { it, (key, byte) ->
                when (key) {
                    "first" -> it.copy(first = byte)
                    "second" -> it.copy(second = byte)
                    "third" -> it.copy(third = byte)
                    "fourth" -> it.copy(fourth = byte)
                    "fifth" -> it.copy(fifth = byte)
                    "sixth" -> it.copy(sixth = byte)
                    "seventh" -> it.copy(seventh = byte)
                    "eighth" -> it.copy(eighth = byte)
                    else -> throw IllegalStateException("Check failed.")
                }
            }
        },
        next = random::nextByte,
    )

    @Test
    fun testUByteOct(): Unit = testAggregateValue(
        constructor = { values ->
            val first by values
            val second by values
            val third by values
            val fourth by values
            val fifth by values
            val sixth by values
            val seventh by values
            val eighth by values
            UByteOct(first, second, third, fourth, fifth, sixth, seventh, eighth)
        },
        getters = mapOf(
            "first" to listOf(UByteOct::first, UByteOct::component1),
            "second" to listOf(UByteOct::second, UByteOct::component2),
            "third" to listOf(UByteOct::third, UByteOct::component3),
            "fourth" to listOf(UByteOct::fourth, UByteOct::component4),
            "fifth" to listOf(UByteOct::fifth, UByteOct::component5),
            "sixth" to listOf(UByteOct::sixth, UByteOct::component6),
            "seventh" to listOf(UByteOct::seventh, UByteOct::component7),
            "eighth" to listOf(UByteOct::eighth, UByteOct::component8),
        ),
        copy = { value, values ->
            values.entries.fold(value) { it, (key, ubyte) ->
                when (key) {
                    "first" -> it.copy(first = ubyte)
                    "second" -> it.copy(second = ubyte)
                    "third" -> it.copy(third = ubyte)
                    "fourth" -> it.copy(fourth = ubyte)
                    "fifth" -> it.copy(fifth = ubyte)
                    "sixth" -> it.copy(sixth = ubyte)
                    "seventh" -> it.copy(seventh = ubyte)
                    "eighth" -> it.copy(eighth = ubyte)
                    else -> throw IllegalStateException("Check failed.")
                }
            }
        },
        next = random::nextUByte,
    )

    private fun <K, C, T> testAggregateValue(
        constructor: (Map<K, T>) -> C,
        getters: Map<K, Iterable<(C) -> T>>,
        copy: (C, Map<K, T>) -> C,
        next: () -> T,
    ): Unit = repeat(100) {
        val values = getters.mapValuesTo(mutableMapOf()) { next() }
        var value = constructor(values)
        for ((key, callables) in getters) {
            val expected = values[key]
            for (callable in callables) assertEquals(expected, callable(value), key.toString())
        }
        repeat(10) {
            val newValues = values.keys.filter { random.nextBoolean() }.associateWith { next() }
            values.putAll(newValues)
            value = copy(value, newValues)
            for ((key, callables) in getters) {
                val expected = values[key]
                for (callable in callables) assertEquals(expected, callable(value), key.toString())
            }
        }
    }

    private val random = Random(AggregateValueTest::class.hashCode())
}
