package com.github.ephemient.kotlin.numeric

import kotlin.math.abs

actual class BigInteger private constructor(
    private val isNegative: Boolean,
    private val storage: UIntArray,
) : Number(), Comparable<BigInteger> {
    init {
        require(if (storage.isEmpty()) !isNegative else storage.last() != 0U)
    }

    internal constructor(isNegative: Boolean, ulong: ULong) : this(
        isNegative = isNegative,
        storage = when {
            ulong == 0UL -> uintArrayOf()
            ulong <= UInt.MAX_VALUE -> uintArrayOf(ulong.toUInt())
            else -> uintArrayOf(ulong.toUInt(), (ulong shr 32).toUInt())
        },
    )

    internal constructor(isNegative: Boolean, uint: UInt) : this(
        isNegative = isNegative,
        storage = if (uint == 0U) uintArrayOf() else uintArrayOf(uint),
    )

    actual fun signum(): Int = when {
        storage.isEmpty() -> 0
        isNegative -> -1
        else -> 1
    }

    actual fun abs(): BigInteger = if (this < ZERO) negate() else this

    actual fun negate(): BigInteger = if (storage.isEmpty()) this else {
        BigInteger(isNegative = !isNegative, storage = storage)
    }

    actual fun add(augend: BigInteger): BigInteger = when {
        storage.isEmpty() -> augend
        augend.storage.isEmpty() -> this
        isNegative != augend.isNegative -> this - -augend
        else -> {
            var carry = false
            val plus = UIntArray(maxOf(storage.size, augend.storage.size)) { i ->
                val a = if (i < storage.size) storage[i] else 0U
                val b = if (i < augend.storage.size) augend.storage[i] else 0U
                (a + b + if (carry) 1U else 0U).also { carry = if (carry) it <= a else it < a }
            }
            BigInteger(isNegative = isNegative, storage = if (carry) plus + 1U else plus)
        }
    }

    actual fun subtract(subtrahend: BigInteger): BigInteger = when {
        storage.isEmpty() -> -subtrahend
        subtrahend.storage.isEmpty() -> this
        isNegative != subtrahend.isNegative -> this + -subtrahend
        abs() < subtrahend.abs() -> -(subtrahend - this)
        else -> {
            var borrow = false
            val minus = UIntArray(storage.size) { i ->
                val a = storage[i]
                val b = if (i < subtrahend.storage.size) subtrahend.storage[i] else 0U
                (a - b - if (borrow) 1U else 0U).also { borrow = if (borrow) it >= a else it > a }
            }
            check(!borrow)
            val n = minus.highestNonZeroIndex()
            if (n < 0) ZERO else BigInteger(isNegative = isNegative, storage = minus.sliceArray(0..n))
        }
    }

    actual fun multiply(multiplicand: BigInteger): BigInteger {
        if (storage.isEmpty() || multiplicand.storage.isEmpty()) return ZERO
        if (storage.size == 1 && multiplicand.storage.size == 1) return BigInteger(
            isNegative = isNegative != multiplicand.isNegative,
            ulong = storage.single().toULong() * multiplicand.storage.single().toULong(),
        )
        val n = maxOf(storage.size, multiplicand.storage.size) / 2
        val (x1, x0) = split(n)
        val (y1, y0) = multiplicand.split(n)
        val z2 = x1 * y1
        val z0 = x0 * y0
        val z1 = z2 + z0 - (x1 - x0) * (y1 - y0)
        return z2.shift(2 * n) + z1.shift(n) + z0
    }

    private fun split(n: Int): Pair<BigInteger, BigInteger> = if (n < storage.size) {
        val m = storage.highestNonZeroIndex(start = n)
        BigInteger(isNegative = isNegative, storage = storage.sliceArray(n until storage.size)) to
            if (m < 0) ZERO else BigInteger(isNegative = isNegative, storage = storage.sliceArray(0..m))
    } else ZERO to this

    private fun shift(n: Int): BigInteger = if (storage.isEmpty()) this else {
        BigInteger(isNegative = isNegative, storage = storage.copyInto(UIntArray(n + storage.size), n))
    }

    @Throws(ArithmeticException::class)
    actual fun divideAndRemainder(divisor: BigInteger): Array<BigInteger> {
        if (divisor.storage.isEmpty()) throw ArithmeticException("/ by zero")
        if (storage.size < divisor.storage.size) return arrayOf(ZERO, this)
        val rem = storage.copyOf()
        val quot = UIntArray(storage.size - divisor.storage.size + 1)
        outer@for (i in quot.size * UInt.SIZE_BITS - 1 downTo 0) {
            inner@for (j in divisor.storage.size * UInt.SIZE_BITS downTo 0) {
                val a = if (j < divisor.storage.size * UInt.SIZE_BITS) {
                    divisor.storage[j / UInt.SIZE_BITS].toInt() shr j % UInt.SIZE_BITS and 1
                } else 0
                val b = if (i + j < rem.size * UInt.SIZE_BITS) {
                    rem[(i + j) / UInt.SIZE_BITS].toInt() shr (i + j) % UInt.SIZE_BITS and 1
                } else 0
                if (a < b) break@inner
                if (a > b) continue@outer
            }
            quot[i / UInt.SIZE_BITS] = quot[i / UInt.SIZE_BITS] or (1U shl i % UInt.SIZE_BITS)
            var borrow = false
            for (j in 0 until rem.size * UInt.SIZE_BITS - i) {
                val a = if (j < divisor.storage.size * UInt.SIZE_BITS) {
                    divisor.storage[j / UInt.SIZE_BITS].toInt() shr j % UInt.SIZE_BITS and 1
                } else if (borrow) 0 else break
                val b = rem[(i + j) / UInt.SIZE_BITS].toInt() shr (i + j) % UInt.SIZE_BITS and 1
                val c = b - a - if (borrow) 1 else 0
                if (b != c and 1) {
                    rem[(i + j) / UInt.SIZE_BITS] =
                        rem[(i + j) / UInt.SIZE_BITS] xor (1U shl (i + j) % UInt.SIZE_BITS)
                }
                borrow = c < 0
            }
            check(!borrow)
        }
        val m = rem.highestNonZeroIndex()
        val n = quot.highestNonZeroIndex()
        return arrayOf(
            if (n < 0) ZERO else BigInteger(isNegative = isNegative != divisor.isNegative, storage = quot.sliceArray(0..n)),
            if (m < 0) ZERO else BigInteger(isNegative = isNegative, storage = rem.sliceArray(0..m)),
        )
    }

    @Throws(ArithmeticException::class)
    actual fun divide(divisor: BigInteger): BigInteger = divideAndRemainder(divisor)[0]

    @Throws(ArithmeticException::class)
    actual fun remainder(divisor: BigInteger): BigInteger = divideAndRemainder(divisor)[1]

    @Throws(ArithmeticException::class)
    actual fun pow(exponent: Int): BigInteger {
        if (exponent < 0) throw ArithmeticException()
        if (exponent == 0) return ONE
        if (exponent == 1) return this
        val base = abs()
        var acc = base
        for (i in storage.size * UInt.SIZE_BITS - exponent.countLeadingZeroBits() - 2 downTo 0) {
            acc *= acc
            if (storage[i / UInt.SIZE_BITS] shr i % UInt.SIZE_BITS and 1U != 0U) acc *= if (i == 0) this else base
        }
        return acc
    }

    override fun toDouble(): Double {
        val lastIndex = storage.indexOfLast { it != 0U }
        var significand = 0L
        val exponent = if (lastIndex < 0) 0L else {
            val clz = storage[lastIndex].countLeadingZeroBits() + 1
            var position = 52 + clz
            for (i in lastIndex downTo 0) {
                if (position <= 0) break
                position -= UInt.SIZE_BITS
                significand = significand or storage[i].let {
                    if (position >= 0) it.toLong() shl position else (it shr -position).toLong()
                } and 0xfffffffffffff
            }
            ((lastIndex + 1L) * UInt.SIZE_BITS - clz + 0x3ff).coerceIn(0, 0x7ff) shl 52
        }
        return Double.fromBits(significand or exponent or if (isNegative) Long.MIN_VALUE else 0)
    }

    override fun toFloat(): Float = toDouble().toFloat()

    override fun toLong(): Long = when (storage.size) {
        0 -> 0L
        1 -> storage[0].toLong()
        else -> storage[1].toLong() shl 32 or storage[0].toLong()
    }.let { if (isNegative) -it else it }

    override fun toInt(): Int = if (storage.isEmpty()) 0 else {
        storage[0].toInt().let { if (isNegative) -it else it }
    }

    override fun toChar(): Char = toInt().toChar()

    override fun toShort(): Short = toInt().toShort()

    override fun toByte(): Byte = toInt().toByte()

    override fun compareTo(other: BigInteger): Int {
        compareValues(other.isNegative, isNegative).let { if (it != 0) return it }
        val a = if (isNegative) other.storage else this.storage
        val b = if (isNegative) this.storage else other.storage
        compareValues(a.size, b.size).let { if (it != 0) return it }
        for (i in a.indices.reversed()) compareValues(a[i], b[i]).let { if (it != 0) return it }
        return 0
    }

    override fun equals(other: Any?): Boolean =
        other is BigInteger && isNegative == other.isNegative && storage contentEquals other.storage

    override fun hashCode(): Int {
        var acc = 0
        for (i in storage.indices.reversed()) acc = 31 * acc + storage[i].toInt()
        return if (isNegative) -acc else acc
    }

    override fun toString(): String = toString(radix = 10)

    actual fun toString(radix: Int): String = buildString {
        when (radix) {
            !in 2..36 -> throw IllegalArgumentException("Requirement failed.")
            2 -> appendNibbles(1)
            4 -> appendNibbles(2)
            8 -> appendNibbles(3)
            16 -> appendNibbles(4)
            32 -> appendNibbles(5)
            else -> {
                val x = radix.toBigInteger()
                var y = abs()
                while (y != ZERO) {
                    val (q, r) = y.divideAndRemainder(x)
                    val d = r.toInt()
                    append((if (d in 0..9) '0' else 'a' - 10) + d)
                    y = q
                }
            }
        }
        deleteRange(indexOfLast { it != '0' } + 1, length)
        if (isEmpty()) append('0') else if (isNegative) append('-')
    }.reversed()

    private fun Appendable.appendNibbles(step: Int) {
        for (position in 0 until storage.size * UInt.SIZE_BITS step step) {
            val index = position / UInt.SIZE_BITS
            val data = storage[index] shr position % UInt.SIZE_BITS or if (
                position + step > UInt.SIZE_BITS && index < storage.lastIndex
            ) storage[index + 1] shl UInt.SIZE_BITS - position % UInt.SIZE_BITS else 0U
            val nibble = data.toInt() and (1 shl step) - 1
            append((if (nibble in 0..9) '0' else 'a' - 10) + nibble)
        }
    }

    companion object {
        val ZERO = BigInteger(isNegative = false, storage = uintArrayOf())
        val ONE = BigInteger(isNegative = false, storage = uintArrayOf(1U))
        val TEN = BigInteger(isNegative = false, storage = uintArrayOf(10U))
    }
}

actual object BigIntegers {
    actual val ZERO: BigInteger
        get() = BigInteger.ZERO
    actual val ONE: BigInteger
        get() = BigInteger.ONE
    actual val TEN: BigInteger
        get() = BigInteger.TEN
}

private fun UIntArray.highestNonZeroIndex(start: Int = size): Int {
    for (i in start - 1 downTo 0) {
        if (this[i] != 0U) return i
    }
    return -1
}

@Throws(IllegalArgumentException::class, NumberFormatException::class)
actual fun String.toBigInteger(radix: Int): BigInteger {
    require(radix in 2..36)
    if (none { it != '-' }) throw NumberFormatException()
    val x = radix.toBigInteger()
    var y = BigInteger.ZERO
    var isNegative = false
    forEachIndexed { i, c ->
        val d = when (c) {
            '-' -> {
                require(i == 0)
                isNegative = true
                return@forEachIndexed
            }
            in '0'..'9' -> c - '0'
            in 'A'..'Z' -> c - 'A' + 10
            in 'a'..'z' -> c - 'a' + 10
            else -> -1
        }
        if (d !in 0 until radix) throw NumberFormatException()
        y = x * y + d.toBigInteger()
    }
    return if (isNegative) -y else y
}

actual fun Long.toBigInteger(): BigInteger = BigInteger(isNegative = this < 0L, ulong = abs(this).toULong())

actual fun ULong.toBigInteger(): BigInteger = BigInteger(isNegative = false, ulong = this)

actual fun Int.toBigInteger(): BigInteger = BigInteger(isNegative = this < 0, uint = abs(this).toUInt())

actual fun UInt.toBigInteger(): BigInteger = toLong().toBigInteger()
