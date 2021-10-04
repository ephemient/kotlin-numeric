package com.github.ephemient.kotlin.numeric

import org.jetbrains.jetCheck.Generator
import org.jetbrains.jetCheck.Generator.booleans
import org.jetbrains.jetCheck.Generator.constant
import org.jetbrains.jetCheck.Generator.from
import org.jetbrains.jetCheck.Generator.integers
import org.jetbrains.jetCheck.ImperativeCommand
import org.jetbrains.jetCheck.PropertyChecker
import org.jetbrains.jetCheck.PropertyChecker.customized
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberFunctions
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalStdlibApi
class JvmAggregateValueTest {
    @Test
    fun testIntPair() {
        testAggregateValue(::IntPair, IntPair::copy, integers(), IntPair::first, IntPair::second)
    }

    @Test
    fun testFloatPair() {
        testAggregateValue(::FloatPair, FloatPair::copy, floats(), FloatPair::first, FloatPair::second)
    }

    @Test
    fun testShortPair() {
        testAggregateValue(::ShortPair, ShortPair::copy, shorts(), ShortPair::first, ShortPair::second)
    }

    @Test
    fun testShortQuad() {
        testAggregateValue(
            ::ShortQuad, ShortQuad::copy, shorts(),
            ShortQuad::first, ShortQuad::second, ShortQuad::third, ShortQuad::fourth,
        )
    }

    @Test
    fun testByteQuad() {
        testAggregateValue(
            ::ByteQuad, ByteQuad::copy, bytes(),
            ByteQuad::first, ByteQuad::second, ByteQuad::third, ByteQuad::fourth,
        )
    }

    @Test
    fun testByteOct() {
        testAggregateValue(
            ::ByteOct, ByteOct::copy, bytes(),
            ByteOct::first, ByteOct::second, ByteOct::third, ByteOct::fourth,
            ByteOct::fifth, ByteOct::sixth, ByteOct::seventh, ByteOct::eighth,
        )
    }
}

private inline fun <reified T : Any, U> testAggregateValue(
    constructor: KFunction<T>,
    copy: KFunction<T>,
    valueGenerator: Generator<U>,
    vararg properties: KProperty1<T, U>,
) {
    customized().testAggregateValue(constructor, copy, valueGenerator, *properties)
}

@Suppress("UnsafeCallOnNullableType")
private inline fun <reified T : Any, U> PropertyChecker.Parameters.testAggregateValue(
    constructor: KFunction<T>,
    copy: KFunction<T>,
    valueGenerator: Generator<U>,
    vararg properties: KProperty1<T, U>,
) {
    checkScenarios {
        ImperativeCommand { env ->
            val initialValues = constructor.parameters.associateWith { env.generateValue(valueGenerator, null) }
            var value = constructor.callBy(initialValues)
            env.logMessage(value.toString())
            assertEquals(initialValues.values.toList(), properties.map { it.get(value) })
            val components = value::class.memberFunctions
                .filter { it.isOperator && it.name.startsWith("component") }
                .associateBy { it.name.removePrefix("component").toInt() - 1 }
            assertEquals(initialValues.values.toList(), List(properties.size) { components[it]!!.call(value) })
            env.executeCommands(
                constant(
                    ImperativeCommand { subEnv ->
                        val values = properties.associateByTo(mutableMapOf(), { it.name }, { it.invoke(value) })
                        value = copy.callBy(
                            copy.parameters.mapNotNull { parameter ->
                                if (parameter.kind != KParameter.Kind.VALUE) return@mapNotNull null
                                val subValue = subEnv.generateValue(
                                    booleans().flatMap {
                                        if (it) valueGenerator else constant<U?>(null)
                                    },
                                    null
                                ) ?: return@mapNotNull null
                                parameter to subValue
                            }.onEach { (parameter, value) -> values[parameter.name!!] = value }.toMap().apply {
                                subEnv.logMessage(
                                    entries.joinToString(prefix = "copy(", postfix = ")") { (parameter, value) ->
                                        "${parameter.name}=$value"
                                    }
                                )
                            } + copy.parameters.filter {
                                it.kind == KParameter.Kind.INSTANCE ||
                                    it.kind == KParameter.Kind.EXTENSION_RECEIVER
                            }.associateWith { value }
                        )
                        val newValues = properties.associateBy({ it.name }, { it.invoke(value) })
                        subEnv.logMessage(value.toString())
                        assertEquals(values, newValues)
                        assertEquals(values.values.toList(), List(properties.size) { components[it]!!.call(value) })
                    }
                )
            )
        }
    }
}

private fun floats(): Generator<Float> = from { data ->
    var float: Float
    do {
        float = Float.fromBits(data.generate(integers()))
    } while (float.isNaN())
    float
}

private fun shorts(min: Short = Short.MIN_VALUE, max: Short = Short.MAX_VALUE): Generator<Short> =
    integers(min.toInt(), max.toInt()).map { it.toShort() }

private fun bytes(min: Byte = Byte.MIN_VALUE, max: Byte = Byte.MAX_VALUE): Generator<Byte> =
    integers(min.toInt(), max.toInt()).map { it.toByte() }
