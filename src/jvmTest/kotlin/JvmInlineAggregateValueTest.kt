package com.github.ephemient.kotlin.numeric

import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmExtensionType
import kotlinx.metadata.KmFunctionExtensionVisitor
import kotlinx.metadata.KmFunctionVisitor
import kotlinx.metadata.KmType
import kotlinx.metadata.jvm.JvmFunctionExtensionVisitor
import kotlinx.metadata.jvm.JvmMethodSignature
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import org.objectweb.asm.Type
import kotlin.reflect.full.declaredFunctions
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class JvmInlineAggregateValueTest {
    @Suppress("NestedBlockDepth")
    @Test
    fun testAllSubjectFunctionsHavePrimitiveParametersAndReturnTypes() {
        val metadata = Subject::class.java.getDeclaredAnnotation(Metadata::class.java)
        val header = KotlinClassHeader(
            kind = metadata.kind,
            metadataVersion = metadata.metadataVersion,
            data1 = metadata.data1,
            data2 = metadata.data2,
            extraString = metadata.extraString,
            packageName = metadata.packageName,
            extraInt = metadata.extraInt,
        )
        val kmClass = assertIs<KotlinClassMetadata.Class>(KotlinClassMetadata.read(assertNotNull(header))).toKmClass()
        for (function in kmClass.functions) {
            with(System.out) {
                append("fun ")
                function.receiverParameterType?.let { append(it).append('.') }
                append(function.name).append('(')
                for ((i, valueParameter) in function.valueParameters.withIndex()) {
                    if (i != 0) append(", ")
                    append(valueParameter.name).append(": ")
                    valueParameter.type?.let { append(it) } ?: append('*')
                }
                append("): ")
                append(function.returnType)
            }
            var jvmMethodSignature: JvmMethodSignature? = null
            function.accept(object : KmFunctionVisitor() {
                override fun visitExtensions(type: KmExtensionType): KmFunctionExtensionVisitor? {
                    val visitor = super.visitExtensions(type)
                    return if (type == JvmFunctionExtensionVisitor.TYPE) {
                        object : JvmFunctionExtensionVisitor(visitor as JvmFunctionExtensionVisitor?) {
                            override fun visit(signature: JvmMethodSignature?) {
                                super.visit(signature)
                                jvmMethodSignature = signature
                            }
                        }
                    } else visitor
                }
            })
            with(assertNotNull(jvmMethodSignature)) {
                println(" = $name:$desc")
                assertContains(primitiveTypes, Type.getReturnType(desc))
                for (type in Type.getArgumentTypes(desc)) {
                    assertContains(primitiveTypes, type)
                }
            }
        }
        assertEquals(Subject::class.declaredFunctions.size, kmClass.functions.size)
    }

    private object Subject {
        fun IntPair.toUnsigned() = UIntPair(
            first = first.toUInt(),
            second = second.toUInt(),
        )
        fun UIntPair.toUnsigned() = IntPair(
            first = first.toInt(),
            second = second.toInt(),
        )
        fun ShortPair.toUnsigned() = UShortPair(
            first = first.toUShort(),
            second = second.toUShort(),
        )
        fun UShortPair.toSigned() = ShortPair(
            first = first.toShort(),
            second = second.toShort(),
        )
        fun ShortQuad.toUnsigned() = UShortQuad(
            first = first.toUShort(),
            second = second.toUShort(),
            third = third.toUShort(),
            fourth = fourth.toUShort(),
        )
        fun UShortQuad.toSigned() = ShortQuad(
            first = first.toShort(),
            second = second.toShort(),
            third = third.toShort(),
            fourth = fourth.toShort(),
        )
        fun ByteQuad.toUnsigned() = UByteQuad(
            first = first.toUByte(),
            second = second.toUByte(),
            third = third.toUByte(),
            fourth = fourth.toUByte(),
        )
        fun UByteQuad.toSigned() = ByteQuad(
            first = first.toByte(),
            second = second.toByte(),
            third = third.toByte(),
            fourth = fourth.toByte(),
        )
        fun ByteOct.toUnsigned() = UByteOct(
            first = first.toUByte(),
            second = second.toUByte(),
            third = third.toUByte(),
            fourth = fourth.toUByte(),
            fifth = fifth.toUByte(),
            sixth = sixth.toUByte(),
            seventh = seventh.toUByte(),
            eighth = eighth.toUByte(),
        )
        fun UByteOct.toSigned() = ByteOct(
            first = first.toByte(),
            second = second.toByte(),
            third = third.toByte(),
            fourth = fourth.toByte(),
            fifth = fifth.toByte(),
            sixth = sixth.toByte(),
            seventh = seventh.toByte(),
            eighth = eighth.toByte(),
        )
    }

    private companion object {
        val primitiveTypes = listOf(
            Type.VOID_TYPE,
            Type.BOOLEAN_TYPE,
            Type.CHAR_TYPE,
            Type.BYTE_TYPE,
            Type.SHORT_TYPE,
            Type.INT_TYPE,
            Type.FLOAT_TYPE,
            Type.LONG_TYPE,
            Type.DOUBLE_TYPE,
        )
    }
}

private fun <T : Appendable> T.append(kmType: KmType): T = apply {
    when (val classifier = kmType.classifier) {
        is KmClassifier.Class -> append(classifier.name.replace('/', '.'))
        is KmClassifier.TypeAlias -> append(classifier.name.replace('/', '.'))
        is KmClassifier.TypeParameter -> append('?').append(classifier.id.toString())
    }
}
