package com.github.ephemient.kotlin.numeric

import kotlin.jvm.JvmInline

@JvmInline
value class CheckedULong @PublishedApi internal constructor(
    @PublishedApi internal val value: ULong,
) : Comparable<CheckedULong> {
    operator fun plus(other: CheckedULong) = CheckedULong(value addExact other.value)
    operator fun minus(other: CheckedULong) = CheckedULong(value subtractExact other.value)
    operator fun times(other: CheckedULong) = CheckedULong(value multiplyExact other.value)
    operator fun div(other: CheckedULong) = CheckedULong(value / other.value)
    operator fun rem(other: CheckedULong) = CheckedULong(value % other.value)
    override fun compareTo(other: CheckedULong): Int = value.compareTo(other.value)
    fun toULong(): ULong = value
    override fun toString(): String = value.toString()
}

fun ULong.toChecked(): CheckedULong = CheckedULong(this)
