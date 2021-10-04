package com.github.ephemient.kotlin.numeric.generator

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.buildCodeBlock
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class ConversionsGenerator : DefaultTask() {
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Input
    abstract val packageName: Property<String>

    @get:Input
    abstract val types: ListProperty<Set<String>>

    @get:[Optional Input]
    abstract val expectExact: SetProperty<Pair<String, String>>

    @Suppress("ComplexMethod", "LongMethod", "NestedBlockDepth")
    @TaskAction
    internal fun execute() {
        val expectExact = expectExact.orNull.orEmpty()
        val outputDirectory = outputDirectory.get().asFile
        outputDirectory.mkdirs()
        outputDirectory.listFiles()?.forEach { it.deleteRecursively() }
        val types = types.get()
        val fileBuilder = FileSpec.builder(packageName.get(), "generatedConversions")
        for ((i, fromTypes) in types.withIndex()) {
            for ((j, toTypes) in types.drop(i).withIndex()) {
                for (fromType in fromTypes) {
                    val fromName = ClassName.bestGuess(fromType)
                    val toFromFunction = "to" + fromName.simpleName.replaceFirstChar { it.titlecase() }
                    for (toType in toTypes) {
                        val toName = ClassName.bestGuess(toType)
                        val toToFunction = "to" + toName.simpleName.replaceFirstChar { it.titlecase() }
                        if (fromName == toName) continue
                        val minValue = MemberName(toName.nestedClass("Companion"), "MIN_VALUE")
                        val maxValue = MemberName(toName.nestedClass("Companion"), "MAX_VALUE")

                        val exactFunBuilder = FunSpec.builder(toToFunction + "Exact")
                            .receiver(fromName)
                            .returns(toName)
                            .addAnnotation(AnnotationSpec.builder(throws)
                                .addMember("%T::class", arithmeticException)
                                .build())
                        if (fromType to toType in expectExact) {
                            exactFunBuilder.addModifiers(KModifier.EXPECT)
                        } else {
                            exactFunBuilder.addCode(buildCodeBlock {
                                beginControlFlow(
                                    "return if (%L)",
                                    when {
                                        fromName.simpleName.startsWith("U") -> buildCodeBlock {
                                            add("this <= %M.%N()", maxValue, toFromFunction)
                                        }
                                        j == 0 -> "this >= 0"
                                        else -> buildCodeBlock {
                                            add(
                                                "this in %M.%N() .. %M.%N()",
                                                minValue, toFromFunction,
                                                maxValue, toFromFunction,
                                            )
                                        }
                                    },
                                )
                                addStatement("this.%N()", toToFunction)
                                nextControlFlow("else")
                                addStatement("throw %T(%S)", arithmeticException, "integer overflow")
                                endControlFlow()
                            })
                        }
                        fileBuilder.addFunction(exactFunBuilder.build())

                        fileBuilder.addFunction(FunSpec.builder(toToFunction + "Saturating")
                            .receiver(fromName)
                            .returns(toName)
                            .addCode(buildCodeBlock {
                                beginControlFlow("return when")
                                when {
                                    fromName.simpleName.startsWith("U") ->
                                        addStatement("this > %M.%N() -> %M", maxValue, toFromFunction, maxValue)
                                    j == 0 -> addStatement("this < 0 -> 0U")
                                    else -> {
                                        addStatement("this < %M.%N() -> %M", minValue, toFromFunction, minValue)
                                        addStatement("this > %M.%N() -> %M", maxValue, toFromFunction, maxValue)
                                    }
                                }
                                addStatement("else -> this.%N()", toToFunction)
                                endControlFlow()
                            })
                            .build())
                    }
                }
            }
        }
        fileBuilder.build().writeTo(outputDirectory)
    }

    private companion object {
        val throws = ClassName("kotlin", "Throws")
        val arithmeticException = ClassName("kotlin", "ArithmeticException")
    }
}
