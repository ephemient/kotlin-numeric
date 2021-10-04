@file:Suppress("UNUSED_VARIABLE", "UnusedPrivateMember", "TooManyFunctions")

package com.github.ephemient.kotlin.numeric

external fun BigInt(value: dynamic): BigInt

external class BigInt {
    fun toString(radix: Int = definedExternally): String
}

actual class BigInteger internal constructor(private val bigint: BigInt) : Number(), Comparable<BigInteger> {
    actual fun signum(): Int {
        val bigint = this.bigint
        return js("bigint < 0 ? -1 : bigint > 0 ? 1 : 0").unsafeCast<Int>()
    }

    actual fun abs(): BigInteger {
        val bigint = this.bigint
        return if (js("bigint < 0").unsafeCast<Boolean>()) negate() else this
    }

    actual fun negate(): BigInteger {
        val bigint = this.bigint
        return BigInteger(js("-bigint").unsafeCast<BigInt>())
    }

    actual fun add(augend: BigInteger): BigInteger {
        val a = this.bigint
        val b = augend.bigint
        return BigInteger(js("a + b").unsafeCast<BigInt>())
    }

    actual fun subtract(subtrahend: BigInteger): BigInteger {
        val a = this.bigint
        val b = subtrahend.bigint
        return BigInteger(js("a - b").unsafeCast<BigInt>())
    }

    actual fun multiply(multiplicand: BigInteger): BigInteger {
        val a = this.bigint
        val b = multiplicand.bigint
        return BigInteger(js("a * b").unsafeCast<BigInt>())
    }

    actual fun divideAndRemainder(divisor: BigInteger): Array<BigInteger> = arrayOf(this / divisor, this % divisor)

    actual fun divide(divisor: BigInteger): BigInteger {
        val a = this.bigint
        val b = divisor.bigint
        return BigInteger(js("a / b").unsafeCast<BigInt>())
    }

    actual fun remainder(divisor: BigInteger): BigInteger {
        val a = this.bigint
        val b = divisor.bigint
        return BigInteger(js("a % b").unsafeCast<BigInt>())
    }

    actual fun pow(exponent: Int): BigInteger {
        val bigint = this.bigint
        val e = BigInt(exponent)
        // eval to work around https://youtrack.jetbrains.com/issue/KT-48980
        return BigInteger(js("""eval("bigint ** e")""").unsafeCast<BigInt>())
    }

    override fun toDouble(): Double {
        val bigint = this.bigint
        return js("Number(bigint)")
    }

    override fun toFloat(): Float = toDouble().toFloat()

    override fun toLong(): Long {
        val bigint = this.bigint
        val hex: String = js("BigInt.asIntN(64, bigint).toString(16)")
        return hex.toLong(radix = 16)
    }

    override fun toInt(): Int {
        val bigint = this.bigint
        return js("Number(BigInt.asIntN(32, bigint))")
    }

    override fun toChar(): Char = toInt().toChar()

    override fun toShort(): Short = toInt().toShort()

    override fun toByte(): Byte = toInt().toByte()

    override fun compareTo(other: BigInteger): Int {
        val a = this.bigint
        val b = other.bigint
        return js("a < b ? -1 : a > b ? 1 : 0")
    }

    override fun equals(other: Any?): Boolean = other is BigInteger && bigint == other.bigint

    override fun hashCode(): Int = bigint.hashCode()

    override fun toString(): String = bigint.toString()

    actual fun toString(radix: Int): String = bigint.toString(radix = radix)

    companion object {
        val ZERO: BigInteger get() = TODO()
        val ONE: BigInteger get() = TODO()
        val TEN: BigInteger get() = TODO()
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

@Suppress("MagicNumber")
actual fun String.toBigInteger(radix: Int): BigInteger = if (radix == 10) BigInteger(BigInt(this)) else {
    require(radix in 2..36)
    if (none { it != '-' }) throw NumberFormatException()
    val x = BigInt(radix)
    var y = BigInt(0)
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
        y = js("x * y + BigInt(d)")
    }
    BigInteger(if (isNegative) js("-y") else y)
}

actual fun Long.toBigInteger(): BigInteger = BigInteger(BigInt(toString()))

actual fun ULong.toBigInteger(): BigInteger = BigInteger(BigInt(toString()))

actual fun Int.toBigInteger(): BigInteger = BigInteger(BigInt(this))

actual fun UInt.toBigInteger(): BigInteger = BigInteger(BigInt(toString()))
