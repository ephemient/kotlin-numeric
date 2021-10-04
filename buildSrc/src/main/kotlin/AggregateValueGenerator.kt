package com.github.ephemient.kotlin.numeric.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.buildCodeBlock
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class AggregateValueGenerator: DefaultTask() {
    @get:OutputDirectory
    val outputDirectory: DirectoryProperty = project.objects.directoryProperty()
        .convention(project.layout.buildDirectory.dir("generated/source/aggregateValues"))

    @get:Nested
    abstract val types: MapProperty<String, Type>

    fun type(name: String, builder: Action<Type>) {
        types.put(name, project.provider { project.objects.newInstance(Type::class.java).apply(builder::execute) })
    }

    @Suppress("ComplexMethod", "LongMethod", "MagicNumber", "SpreadOperator")
    @TaskAction
    internal fun execute() {
        val outputDirectory = outputDirectory.get().asFile
        outputDirectory.mkdirs()
        outputDirectory.listFiles()?.forEach { it.deleteRecursively() }
        for ((name, type) in types.get().entries) {
            val className = ClassName.bestGuess(name)
            val componentNames = type.componentNames.get()
            val componentType = ClassName.bestGuess(type.componentType.get())
            val bitsPerComponent = type.bitsPerComponent.get()
            val componentToBits = type.componentToBits.orNull?.ifEmpty { null }
                ?: "(%L).toU${componentType.simpleName.removePrefix("U")}()"
            val bitsToComponent = type.bitsToComponent.orNull?.ifEmpty { null }
                ?: "(%L).to${componentType.simpleName}()"
            val (valueType, bitsToValue) = when (val valueBits = componentNames.size * bitsPerComponent) {
                in (0..UInt.SIZE_BITS) -> ClassName("kotlin", "UInt") to "(%L).toUInt()"
                in (0..ULong.SIZE_BITS) -> ClassName("kotlin", "ULong") to "(%L).toULong()"
                else -> throw IllegalArgumentException("$valueBits !in 0..64")
            }
            FileSpec.builder(className.packageName, "generated" + className.simpleNames.joinToString(""))
                .addType(TypeSpec.classBuilder(className)
                    .addKdoc(
                        "Represents a %L of [%T] values as a single [%T] value.",
                        if (componentNames.size == 2) "pair" else "${componentNames.size}-tuple",
                        componentType,
                        valueType,
                    )
                    .addAnnotation(ClassName("kotlin.jvm", "JvmInline"))
                    .addModifiers(KModifier.VALUE)
                    .primaryConstructor(FunSpec.constructorBuilder()
                        .addModifiers(KModifier.PRIVATE)
                        .addParameter("value", valueType)
                        .build())
                    .addProperty(PropertySpec.builder("value", valueType)
                        .addModifiers(KModifier.PRIVATE)
                        .initializer("value")
                        .build())
                    .addFunction(FunSpec.constructorBuilder()
                        .addParameters(componentNames.map { ParameterSpec.builder(it, componentType).build() })
                        .callThisConstructor(buildCodeBlock {
                            componentNames.forEachIndexed { i, componentName ->
                                if (i == 0) add("%N = ", "value") else add(" or ")
                                add("((%L) shl %L)", buildCodeBlock {
                                    add(bitsToValue, buildCodeBlock {
                                        add(componentToBits, componentName)
                                    })
                                }, i * bitsPerComponent)
                            }
                        })
                        .build())
                    .addProperties(componentNames.mapIndexed { i, componentName ->
                        PropertySpec.builder(componentName, componentType)
                            .getter(FunSpec.getterBuilder()
                                .addCode("return %L", buildCodeBlock {
                                    add(bitsToComponent, buildCodeBlock {
                                        add("%N shr %L", "value", i * bitsPerComponent)
                                    })
                                })
                                .build())
                            .build()
                    })
                    .addFunctions(componentNames.mapIndexed { i, componentName ->
                        FunSpec.builder("component${i + 1}")
                            .addModifiers(KModifier.OPERATOR)
                            .returns(componentType)
                            .addCode("return %N", componentName)
                            .build()
                    })
                    .addFunction(FunSpec.builder("copy")
                        .addParameters(componentNames.map { componentName ->
                            ParameterSpec.builder(componentName, componentType)
                                .defaultValue("this.%N", componentName)
                                .build()
                        })
                        .returns(className)
                        .addCode(componentNames
                            .joinToString(prefix = "return %T(", postfix = ")") { "%N = %N" },
                            className,
                            *componentNames.flatMap { listOf(it, it) }.toTypedArray())
                        .build())
                    .addFunction(FunSpec.builder("toString")
                        .addModifiers(KModifier.OVERRIDE)
                        .returns(STRING)
                        .addCode(componentNames
                            .joinToString(separator = ",·", prefix = "return \"(", postfix = ")\"") { "\${%N}" },
                            *componentNames.toTypedArray())
                        .build())
                    .build())
                .build()
                .writeTo(outputDirectory)
        }
    }

    abstract class Type {
        @get:Input
        abstract val componentNames: ListProperty<String>

        @get:Input
        abstract val componentType: Property<String>

        @get:Input
        abstract val bitsPerComponent: Property<Int>

        @get:[Optional Input]
        abstract val componentToBits: Property<String>

        @get:[Optional Input]
        abstract val bitsToComponent: Property<String>

        fun components(type: String, bits: Int, vararg names: String) {
            componentType.set(type)
            bitsPerComponent.set(bits)
            componentNames.addAll(*names)
        }
    }
}
