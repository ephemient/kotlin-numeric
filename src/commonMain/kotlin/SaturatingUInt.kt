package com.github.ephemient.kotlin.numeric

import kotlin.jvm.JvmInline

@JvmInline
value class SaturatingUInt @PublishedApi internal constructor(
    @PublishedApi internal val value: UInt,
) : Comparable<SaturatingUInt> {
    operator fun plus(other: SaturatingUInt) = SaturatingUInt(value addSaturating other.value)
    operator fun minus(other: SaturatingUInt) = SaturatingUInt(value subtractSaturating other.value)
    operator fun times(other: SaturatingUInt) = SaturatingUInt(value multiplySaturating other.value)
    operator fun div(other: SaturatingUInt) = SaturatingUInt(value / other.value)
    operator fun rem(other: SaturatingUInt) = SaturatingUInt(value % other.value)
    override fun compareTo(other: SaturatingUInt): Int = value.compareTo(other.value)
    fun toUInt(): UInt = value
    override fun toString(): String = value.toString()
}

fun UInt.toSaturating(): SaturatingUInt = SaturatingUInt(this)
