package com.github.ephemient.kotlin.numeric.generator

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.COLLECTION
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
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
            val arrayClassName = ClassName.bestGuess(name + "Array")
            val iteratorClassName = ClassName.bestGuess(name + "Iterator")
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
            val backingArrayType = ClassName(
                valueType.packageName,
                valueType.simpleNames.toMutableList().apply {
                    set(lastIndex, get(lastIndex) + "Array")
                }
            )
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
                        .addAnnotation(PublishedApi::class)
                        .addModifiers(KModifier.INTERNAL)
                        .addParameter("value", valueType)
                        .build())
                    .addProperty(PropertySpec.builder("value", valueType)
                        .addAnnotation(PublishedApi::class)
                        .addModifiers(KModifier.INTERNAL)
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
                                .addStatement("return %L", buildCodeBlock {
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
                            .addStatement("return %N", componentName)
                            .build()
                    })
                    .addFunction(FunSpec.builder("copy")
                        .addParameters(componentNames.map { componentName ->
                            ParameterSpec.builder(componentName, componentType)
                                .defaultValue("this.%N", componentName)
                                .build()
                        })
                        .returns(className)
                        .addStatement(componentNames
                            .joinToString(prefix = "return %T(", postfix = ")") { "%N = %N" },
                            className,
                            *componentNames.flatMap { listOf(it, it) }.toTypedArray())
                        .build())
                    .addFunction(FunSpec.builder("toString")
                        .addModifiers(KModifier.OVERRIDE)
                        .returns(STRING)
                        .addStatement(componentNames
                            .joinToString(separator = ",·", prefix = "return \"(", postfix = ")\"") { "\${%N}" },
                            *componentNames.toTypedArray())
                        .build())
                    .build())
                .addType(TypeSpec.classBuilder(arrayClassName)
                    .addAnnotation(ClassName("kotlin.jvm", "JvmInline"))
                    .addModifiers(KModifier.VALUE)
                    .addSuperinterface(COLLECTION.parameterizedBy(className))
                    .primaryConstructor(FunSpec.constructorBuilder()
                        .addAnnotation(PublishedApi::class)
                        .addModifiers(KModifier.INTERNAL)
                        .addParameter("storage", backingArrayType)
                        .build())
                    .addProperty(PropertySpec.builder("storage", backingArrayType)
                        .addAnnotation(PublishedApi::class)
                        .addModifiers(KModifier.INTERNAL)
                        .initializer("storage")
                        .build())
                    .addFunction(FunSpec.constructorBuilder()
                        .addParameter("size", INT)
                        .callThisConstructor(CodeBlock.of("%T(%N)", backingArrayType, "size"))
                        .build())
                    .addFunction(FunSpec.builder("get")
                        .addModifiers(KModifier.OPERATOR)
                        .addParameter("index", INT)
                        .returns(className)
                        .addStatement("return %T(%N[%N])", className, "storage", "index")
                        .build())
                    .addFunction(FunSpec.builder("set")
                        .addModifiers(KModifier.OPERATOR)
                        .addParameter("index", INT)
                        .addParameter("value", className)
                        .addStatement("%N[%N] = %N.%N", "storage", "index", "value", "value")
                        .build())
                    .addProperty(PropertySpec.builder("size", INT)
                        .addModifiers(KModifier.OVERRIDE)
                        .getter(FunSpec.getterBuilder()
                            .addStatement("return %N.%N", "storage", "size")
                            .build())
                        .build())
                    .addFunction(FunSpec.builder("iterator")
                        .addModifiers(KModifier.OVERRIDE)
                        .returns(iteratorClassName)
                        .addStatement("return %T(%N)", arrayClassName.nestedClass("Iterator"), "storage")
                        .build())
                    .addFunction(FunSpec.builder("contains")
                        .addModifiers(KModifier.OVERRIDE)
                        .addParameter("element", className)
                        .returns(BOOLEAN)
                        .addStatement("return %N.%N(%N.%N)", "storage", "contains", "element", "value")
                        .build())
                    .addFunction(FunSpec.builder("containsAll")
                        .addModifiers(KModifier.OVERRIDE)
                        .addParameter("elements", COLLECTION.parameterizedBy(className))
                        .returns(BOOLEAN)
                        .addStatement(
                            "return %N.%N·{ %N.%N(%N.%N) }",
                            "elements",
                            "all",
                            "storage",
                            "contains",
                            "it",
                            "value",
                        )
                        .build())
                    .addFunction(FunSpec.builder("isEmpty")
                        .addModifiers(KModifier.OVERRIDE)
                        .returns(BOOLEAN)
                        .addStatement("return %N == %L", "size", 0)
                        .build())
                    .addType(TypeSpec.classBuilder("Iterator")
                        .addSuperinterface(iteratorClassName)
                        .primaryConstructor(FunSpec.constructorBuilder()
                            .addModifiers(KModifier.INTERNAL)
                            .addParameter("storage", backingArrayType)
                            .build())
                        .addProperty(PropertySpec.builder("storage", backingArrayType)
                            .addModifiers(KModifier.PRIVATE)
                            .initializer("storage")
                            .build())
                        .addProperty(PropertySpec.builder("index", INT)
                            .addModifiers(KModifier.PRIVATE)
                            .mutable()
                            .initializer("%L", 0)
                            .build())
                        .addFunction(FunSpec.builder("hasNext")
                            .addModifiers(KModifier.OVERRIDE)
                            .returns(BOOLEAN)
                            .addStatement("return %N < %N.%N", "index", "storage", "size")
                            .build())
                        .addFunction(FunSpec.builder("next${className.simpleName}")
                            .addModifiers(KModifier.OVERRIDE)
                            .returns(className)
                            .beginControlFlow("return if (%N < %N.%N)", "index", "storage", "size")
                            .addStatement("%T(%N[%N++])", className, "storage", "index")
                            .nextControlFlow("else")
                            .addStatement(
                                "throw %T(%N.%N())",
                                ClassName("kotlin", "NoSuchElementException"),
                                "index",
                                "toString",
                            )
                            .endControlFlow()
                            .build())
                        .build())
                    .build())
                .addType(TypeSpec.interfaceBuilder(iteratorClassName)
                    .addSuperinterface(ClassName("kotlin.collections", "Iterator").parameterizedBy(className))
                    .addFunction(FunSpec.builder("next${className.simpleName}")
                        .addModifiers(KModifier.ABSTRACT)
                        .returns(className)
                        .build())
                    .addFunction(FunSpec.builder("next")
                        .addAnnotation(AnnotationSpec.builder(Deprecated::class)
                            .addMember("message = %S", "Use [next${className.simpleName}] instead.")
                            .addMember("replaceWith = %T(%S)", ReplaceWith::class, "next${className.simpleName}()")
                            .build())
                        .addModifiers(KModifier.OVERRIDE)
                        .returns(className)
                        .addStatement("return %N()", "next${className.simpleName}")
                        .build())
                    .build())
                .addFunction(FunSpec.builder(arrayClassName.simpleName)
                    .addKdoc(
                        @Suppress("MaxLineLength")
                        """
                        |Creates a new array of the specified [size], where each element is calculated by calling the specified [init] function.
                        |
                        |The function [init] is called for each array element sequentially starting from the first one. It should return the value for an array element given its index.
                        """.trimMargin()
                    )
                    .addModifiers(KModifier.INLINE)
                    .addParameter("size", INT)
                    .addParameter("init", LambdaTypeName.get(INT, returnType = className))
                    .addStatement(
                        "return %T(%T(%N) { %N -> %N(%N).%N })",
                        arrayClassName,
                        backingArrayType,
                        "size",
                        "index",
                        "init",
                        "index",
                        "value",
                    )
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
