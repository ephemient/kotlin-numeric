package com.github.ephemient.kotlin.numeric.agent

import com.github.ephemient.kotlin.numeric.annotations.CheckedArithmetic
import com.github.ephemient.kotlin.numeric.annotations.CheckedArithmetic.Mode
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.lang.IllegalArgumentException
import java.lang.instrument.ClassFileTransformer
import java.lang.instrument.IllegalClassFormatException
import java.security.ProtectionDomain
import kotlin.jvm.Throws

internal class NumericTransformer(private val args: Map<String, Mode>) : ClassFileTransformer {
    @Throws(IllegalClassFormatException::class)
    override fun transform(
        loader: ClassLoader,
        className: String,
        classBeingRedefined: Class<*>?,
        protectionDomain: ProtectionDomain,
        classfileBuffer: ByteArray,
    ): ByteArray? {
        val mode = generateSequence(className.replace('/', '.').replace('$', '.')) {
            it.substringBeforeLast('.', "").ifEmpty { null }
        }.firstNotNullOfOrNull { args[it] } ?: Mode.UNCHECKED
        val reader = ClassReader(classfileBuffer)
        if (reader.access and (Opcodes.ACC_ANNOTATION or Opcodes.ACC_MODULE) != 0) return null
        val writer = ClassWriter(reader, 0)
        var didTransform = false
        reader.accept(object : ClassVisitor(Opcodes.ASM9, writer) {
            private var classMode = mode

            override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor = visitAnnotation(
                descriptor,
                super.visitAnnotation(descriptor, visible)
            ) { classMode = it }

            override fun visitMethod(
                access: Int,
                name: String,
                descriptor: String,
                signature: String?,
                exceptions: Array<out String>?
            ): MethodVisitor = object : MethodVisitor(
                Opcodes.ASM9,
                super.visitMethod(access, name, descriptor, signature, exceptions)
            ) {
                private var methodMode = classMode

                override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor = visitAnnotation(
                    descriptor,
                    super.visitAnnotation(descriptor, visible)
                ) { methodMode = it }

                override fun visitInsn(opcode: Int) {
                    if (methodMode == Mode.CHECKED) {
                        when (opcode) {
                            Opcodes.IADD -> {
                                didTransform = true
                                return super.visitMethodInsn(
                                    Opcodes.INVOKESTATIC,
                                    "java/lang/Math",
                                    "addExact",
                                    "(II)I",
                                    false
                                )
                            }
                            Opcodes.LADD -> {
                                didTransform = true
                                return super.visitMethodInsn(
                                    Opcodes.INVOKESTATIC,
                                    "java/lang/Math",
                                    "addExact",
                                    "(JJ)J",
                                    false
                                )
                            }
                            Opcodes.ISUB -> {
                                didTransform = true
                                return super.visitMethodInsn(
                                    Opcodes.INVOKESTATIC,
                                    "java/lang/Math",
                                    "subtractExact",
                                    "(II)I",
                                    false
                                )
                            }
                            Opcodes.LSUB -> {
                                didTransform = true
                                return super.visitMethodInsn(
                                    Opcodes.INVOKESTATIC,
                                    "java/lang/Math",
                                    "subtractExact",
                                    "(JJ)J",
                                    false
                                )
                            }
                            Opcodes.IMUL -> {
                                didTransform = true
                                return super.visitMethodInsn(
                                    Opcodes.INVOKESTATIC,
                                    "java/lang/Math",
                                    "multiplyExact",
                                    "(II)I",
                                    false
                                )
                            }
                            Opcodes.LMUL -> {
                                didTransform = true
                                return super.visitMethodInsn(
                                    Opcodes.INVOKESTATIC,
                                    "java/lang/Math",
                                    "multiplyExact",
                                    "(JJ)J",
                                    false
                                )
                            }
                        }
                    }
                    super.visitInsn(opcode)
                }
            }
        }, 0)
        return if (didTransform) writer.toByteArray() else null
    }
}

private val Class<*>.binaryName: String
    get() = name.replace('.', '/')

private fun visitAnnotation(
    descriptor: String,
    visitor: AnnotationVisitor,
    onMode: (Mode) -> Unit,
): AnnotationVisitor = if (descriptor == "L${CheckedArithmetic::class.java.binaryName};") {
    object : AnnotationVisitor(Opcodes.ASM9, visitor) {
        private var annotationMode = Mode.CHECKED

        override fun visitEnum(name: String, descriptor: String, value: String) {
            super.visitEnum(name, descriptor, value)
            if (name == CheckedArithmetic::mode.name && descriptor == "L${Mode::class.java.binaryName};") {
                try {
                    annotationMode = Mode.valueOf(value)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }
        }

        override fun visitEnd() {
            super.visitEnd()
            onMode(annotationMode)
        }
    }
} else visitor
