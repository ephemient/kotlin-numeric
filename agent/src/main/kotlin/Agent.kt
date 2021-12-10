package com.github.ephemient.kotlin.numeric.agent

import com.github.ephemient.kotlin.numeric.annotations.CheckedArithmetic.Mode
import java.lang.instrument.Instrumentation

object Agent {
    @JvmStatic
    fun premain(agentArgs: String?, inst: Instrumentation) {
        main(agentArgs, inst)
    }

    @JvmStatic
    fun agentmain(agentArgs: String?, inst: Instrumentation) {
        main(agentArgs, inst)
    }

    private fun main(agentArgs: String?, inst: Instrumentation) {
        val args = agentArgs?.ifEmpty { null }?.split(':')?.associate {
            val name = it.substringBeforeLast('=')
            val mode = try {
                Mode.valueOf(it.substringAfterLast('=', Mode.CHECKED.name))
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                Mode.UNCHECKED
            }
            name to mode
        }.orEmpty()
        inst.addTransformer(NumericTransformer(args))
    }
}
