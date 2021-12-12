package com.github.ephemient.kotlin.numeric

import kotlin.jvm.JvmInline

@JvmInline
value class CheckedLong @PublishedApi internal constructor(
    @PublishedApi internal val value: Long,
) : Comparable<CheckedLong> {
    operator fun plus(other: CheckedLong) = CheckedLong(value addExact other.value)
    operator fun minus(other: CheckedLong) = CheckedLong(value subtractExact other.value)
    operator fun times(other: CheckedLong) = CheckedLong(value multiplyExact other.value)
    operator fun div(other: CheckedLong) = CheckedLong(value divideExact other.value)
    operator fun rem(other: CheckedLong) = CheckedLong(value % other.value)
    override fun compareTo(other: CheckedLong): Int = value.compareTo(other.value)
    fun toLong(): Long = value
    override fun toString(): String = value.toString()
}

fun Long.toChecked(): CheckedLong = CheckedLong(this)
