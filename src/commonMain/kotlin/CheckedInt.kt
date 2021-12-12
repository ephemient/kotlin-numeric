package com.github.ephemient.kotlin.numeric

import kotlin.jvm.JvmInline

@JvmInline
value class CheckedInt @PublishedApi internal constructor(
    @PublishedApi internal val value: Int,
) : Comparable<CheckedInt> {
    operator fun plus(other: CheckedInt) = CheckedInt(value addExact other.value)
    operator fun minus(other: CheckedInt) = CheckedInt(value subtractExact other.value)
    operator fun times(other: CheckedInt) = CheckedInt(value multiplyExact other.value)
    operator fun div(other: CheckedInt) = CheckedInt(value divideExact other.value)
    operator fun rem(other: CheckedInt) = CheckedInt(value % other.value)
    override fun compareTo(other: CheckedInt): Int = value.compareTo(other.value)
    fun toInt(): Int = value
    override fun toString(): String = value.toString()
}

fun Int.toChecked(): CheckedInt = CheckedInt(this)
