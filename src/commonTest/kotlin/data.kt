package com.github.ephemient.kotlin.numeric

val interestingLongs = longArrayOf(
    Long.MIN_VALUE, -0x80000001L, -0x80000000L, -0x8001L, -0x8000L, -0x81L, -0x80L, -1L,
    0L, 1L, 0x7FL, 0x80L, 0x7FFFL, 0x8000L, 0x7FFFFFFFL, 0x80000000L, Long.MAX_VALUE,
)

val interestingULongs = ulongArrayOf(
    ULong.MIN_VALUE, 1UL, 0x7FUL, 0x80UL, 0x7FFFUL, 0x8000UL, 0x7FFFFFFFUL, 0x80000000UL, ULong.MAX_VALUE,
)

val interestingInts = intArrayOf(
    Int.MIN_VALUE, -0x8001, -0x8000, -0x81, -0x80, -1, 0, 1, 0x7F, 0x80, 0x7FFF, 0x8000, Int.MAX_VALUE,
)

val interestingUInts = uintArrayOf(UInt.MIN_VALUE, 1U, 0x7FU, 0x80U, 0x7FFFU, 0x8000U, UInt.MAX_VALUE)

val interestingShorts = shortArrayOf(Short.MIN_VALUE, -0x81, -0x80, -1, 0, 1, 0x7F, 0x80, Short.MAX_VALUE)

val interestingUShorts = ushortArrayOf(UShort.MIN_VALUE, 1U, 0x7FU, 0x80U, UShort.MAX_VALUE)

val interestingBytes = byteArrayOf(Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE)

val interestingUBytes = ubyteArrayOf(UByte.MIN_VALUE, 1U, UByte.MAX_VALUE)
