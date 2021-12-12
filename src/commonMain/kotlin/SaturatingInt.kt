package com.github.ephemient.kotlin.numeric

import kotlin.jvm.JvmInline

@JvmInline
value class SaturatingInt @PublishedApi internal constructor(
    @PublishedApi internal val value: Int,
) : Comparable<SaturatingInt> {
    operator fun plus(other: SaturatingInt) = SaturatingInt(value addSaturating other.value)
    operator fun minus(other: SaturatingInt) = SaturatingInt(value subtractSaturating other.value)
    operator fun times(other: SaturatingInt) = SaturatingInt(value multiplySaturating other.value)
    operator fun div(other: SaturatingInt) = SaturatingInt(value divideSaturating other.value)
    operator fun rem(other: SaturatingInt) = SaturatingInt(value % other.value)
    override fun compareTo(other: SaturatingInt): Int = value.compareTo(other.value)
    fun toInt(): Int = value
    override fun toString(): String = value.toString()
}

fun Int.toSaturating(): SaturatingInt = SaturatingInt(this)
