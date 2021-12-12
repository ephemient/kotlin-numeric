package com.github.ephemient.kotlin.numeric

import kotlin.jvm.JvmInline

@JvmInline
value class SaturatingULong @PublishedApi internal constructor(
    @PublishedApi internal val value: ULong,
) : Comparable<SaturatingULong> {
    operator fun plus(other: SaturatingULong) = SaturatingULong(value addSaturating other.value)
    operator fun minus(other: SaturatingULong) = SaturatingULong(value subtractSaturating other.value)
    operator fun times(other: SaturatingULong) = SaturatingULong(value multiplySaturating other.value)
    operator fun div(other: SaturatingULong) = SaturatingULong(value / other.value)
    operator fun rem(other: SaturatingULong) = SaturatingULong(value % other.value)
    override fun compareTo(other: SaturatingULong): Int = value.compareTo(other.value)
    fun toULong(): ULong = value
    override fun toString(): String = value.toString()
}

fun ULong.toSaturating(): SaturatingULong = SaturatingULong(this)
