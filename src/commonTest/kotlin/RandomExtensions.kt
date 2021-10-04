package com.github.ephemient.kotlin.numeric

import kotlin.random.Random

fun Random.nextULong(): ULong = nextLong().toULong()

fun Random.nextUInt(): UInt = nextInt().toUInt()

fun Random.nextShort(): Short = nextBits(Short.SIZE_BITS).toShort()

fun Random.nextUShort(): UShort = nextBits(UShort.SIZE_BITS).toUShort()

fun Random.nextByte(): Byte = nextBits(Byte.SIZE_BITS).toByte()

fun Random.nextUByte(): UByte = nextBits(UByte.SIZE_BITS).toUByte()

fun Random.nextLongGeometric(): Long {
    val n = nextInt(Long.SIZE_BITS) + 1
    return (nextBits(n - Int.SIZE_BITS).toLong() shl 32 or nextBits(n).toUInt().toLong()) - (1 shl n - 1)
}

fun Random.nextULongGeometric(): ULong {
    val n = nextInt(Long.SIZE_BITS) + 1
    return (nextBits(n - Int.SIZE_BITS).toULong() shl 32 or nextBits(n).toUInt().toULong())
}

fun Random.nextIntGeometric(): Int {
    val n = nextInt(Int.SIZE_BITS) + 1
    return nextBits(n) - (1 shl n - 1)
}

fun Random.nextUIntGeometric(): UInt {
    val n = nextInt(Int.SIZE_BITS) + 1
    return nextBits(n).toUInt()
}
