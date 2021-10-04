package com.github.ephemient.kotlin.numeric

import kotlin.test.Asserter
import kotlin.test.asserter

@Throws(AssertionError::class)
inline fun <reified T : Throwable> assertThrows(
    message: String? = null,
    block: () -> Unit,
) = asserter.assertThrows<T>(message, block)

@Throws(AssertionError::class)
inline fun <reified T : Throwable> Asserter.assertThrows(
    message: String? = null,
    block: () -> Unit,
) {
    try {
        block()
    } catch (e: Throwable) {
        if (e is T) return
        fail(message, e)
    }
    fail(message)
}
