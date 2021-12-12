package com.github.ephemient.kotlin.numeric

import kotlin.jvm.JvmInline

@JvmInline
value class CheckedUInt @PublishedApi internal constructor(
    @PublishedApi internal val value: UInt,
) : Comparable<CheckedUInt> {
    operator fun plus(other: CheckedUInt) = CheckedUInt(value addExact other.value)
    operator fun minus(other: CheckedUInt) = CheckedUInt(value subtractExact other.value)
    operator fun times(other: CheckedUInt) = CheckedUInt(value multiplyExact other.value)
    operator fun div(other: CheckedUInt) = CheckedUInt(value / other.value)
    operator fun rem(other: CheckedUInt) = CheckedUInt(value % other.value)
    override fun compareTo(other: CheckedUInt): Int = value.compareTo(other.value)
    fun toUInt(): UInt = value
    override fun toString(): String = value.toString()
}

fun UInt.toChecked(): CheckedUInt = CheckedUInt(this)
