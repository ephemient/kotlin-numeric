package com.github.ephemient.kotlin.numeric

import kotlin.jvm.JvmInline

@JvmInline
value class SaturatingLong @PublishedApi internal constructor(
    @PublishedApi internal val value: Long,
) : Comparable<SaturatingLong> {
    operator fun plus(other: SaturatingLong) = SaturatingLong(value addSaturating other.value)
    operator fun minus(other: SaturatingLong) = SaturatingLong(value subtractSaturating other.value)
    operator fun times(other: SaturatingLong) = SaturatingLong(value multiplySaturating other.value)
    operator fun div(other: SaturatingLong) = SaturatingLong(value divideSaturating other.value)
    operator fun rem(other: SaturatingLong) = SaturatingLong(value % other.value)
    override fun compareTo(other: SaturatingLong): Int = value.compareTo(other.value)
    fun toLong(): Long = value
    override fun toString(): String = value.toString()
}

fun Long.toSaturating(): SaturatingLong = SaturatingLong(this)
