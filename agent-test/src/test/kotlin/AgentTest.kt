package com.github.ephemient.kotlin.numeric.agent.test

import com.github.ephemient.kotlin.numeric.annotations.CheckedArithmetic
import com.github.ephemient.kotlin.numeric.annotations.CheckedArithmetic.Mode
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

@CheckedArithmetic
class AgentTest {
    @Test
    fun testChecked() {
        val x = Int.MAX_VALUE
        val y = Int.MAX_VALUE
        assertThrows(ArithmeticException::class.java) { x + y }
    }

    @CheckedArithmetic(mode = Mode.UNCHECKED)
    @Test
    fun testUnchecked() {
        val x = Int.MAX_VALUE
        val y = Int.MAX_VALUE
        x + y
    }
}
