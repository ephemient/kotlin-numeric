import com.github.ephemient.kotlin.numeric.generator.AggregateValueGenerator
import com.github.ephemient.kotlin.numeric.generator.ConversionsGenerator
import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.language.base.plugins.LifecycleBasePlugin.VERIFICATION_GROUP
import org.jmailen.gradle.kotlinter.tasks.FormatTask
import org.jmailen.gradle.kotlinter.tasks.LintTask

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.dependency.updates)
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlinter)
}

@Suppress("MagicNumber")
val generateAggregateValues by tasks.registering(AggregateValueGenerator::class) {
    type("com.github.ephemient.kotlin.numeric.IntPair") {
        components("kotlin.Int", 32, "first", "second")
    }
    type("com.github.ephemient.kotlin.numeric.UIntPair") {
        components("kotlin.UInt", 32, "first", "second")
    }
    type("com.github.ephemient.kotlin.numeric.FloatPair") {
        components("kotlin.Float", 32, "first", "second")
        bitsToComponent.set("Float.fromBits((%L).toInt())")
        componentToBits.set("(%L).toRawBits().toUInt()")
    }
    type("com.github.ephemient.kotlin.numeric.ShortPair") {
        components("kotlin.Short", 16, "first", "second")
    }
    type("com.github.ephemient.kotlin.numeric.UShortPair") {
        components("kotlin.UShort", 16, "first", "second")
    }
    type("com.github.ephemient.kotlin.numeric.ShortQuad") {
        components("kotlin.Short", 16, "first", "second", "third", "fourth")
    }
    type("com.github.ephemient.kotlin.numeric.UShortQuad") {
        components("kotlin.UShort", 16, "first", "second", "third", "fourth")
    }
    type("com.github.ephemient.kotlin.numeric.ByteQuad") {
        components("kotlin.Byte", 8, "first", "second", "third", "fourth")
    }
    type("com.github.ephemient.kotlin.numeric.UByteQuad") {
        components("kotlin.UByte", 8, "first", "second", "third", "fourth")
    }
    type("com.github.ephemient.kotlin.numeric.ByteOct") {
        components("kotlin.Byte", 8, "first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eighth")
    }
    type("com.github.ephemient.kotlin.numeric.UByteOct") {
        components("kotlin.UByte", 8, "first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eighth")
    }
}

val generateConversions by tasks.registering(ConversionsGenerator::class) {
    outputDirectory.set(layout.buildDirectory.dir("generated/source/commonConversions"))
    packageName.set("com.github.ephemient.kotlin.numeric")
    types.add(setOf("kotlin.Long", "kotlin.ULong"))
    types.add(setOf("kotlin.Int", "kotlin.UInt"))
    types.add(setOf("kotlin.Short", "kotlin.UShort"))
    types.add(setOf("kotlin.Byte", "kotlin.UByte"))
    expectExact.add("kotlin.Long" to "kotlin.Int")
}

val nativeTargets = setOf("linuxX64", "linuxArm64", "mingwX86", "mingwX64", "macosX64", "macosArm64")

kotlin {
    jvm()
    js(BOTH) {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        nodejs {
            testTask {
                useMocha()
            }
        }
    }
    for (nativeTarget in nativeTargets) {
        targetFromPreset(presets[nativeTarget])
    }
    targets.all {
        compilations.all {
            kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.ExperimentalUnsignedTypes"
        }
    }

    sourceSets {
        for ((compilation, parentCompilation) in mapOf("Main" to null, "Test" to "Main")) {
            val nonJvm = create("nonJvm$compilation") {
                dependsOn(getByName("common$compilation"))
                parentCompilation?.also { dependsOn(getByName("nonJvm$it")) }
            }
            getByName("js$compilation") {
                dependsOn(nonJvm)
                parentCompilation?.also { dependsOn(getByName("js$it")) }
            }
            val native = create("native$compilation") {
                dependsOn(nonJvm)
                parentCompilation?.also { dependsOn(getByName("native$it")) }
            }
            val parents = nativeTargets.groupingBy { nativeTarget -> nativeTarget.takeWhile { it.isLowerCase() } }
                .eachCountTo(mutableMapOf())
                .apply { values.retainAll { it > 1 } }
                .mapValues { (nativeParent, _) ->
                    create("$nativeParent$compilation") {
                        dependsOn(native)
                        parentCompilation?.also { dependsOn(getByName("$nativeParent$it")) }
                    }
                }
            for (nativeTarget in nativeTargets) {
                getByName("$nativeTarget$compilation") {
                    dependsOn(parents[nativeTarget.takeWhile { it.isLowerCase() }] ?: native)
                    parentCompilation?.also { dependsOn(getByName("$nativeTarget$it")) }
                }
            }
        }

        getByName("commonMain") {
            kotlin.srcDir(generateAggregateValues.flatMap { it.outputDirectory })
            kotlin.srcDir(generateConversions.flatMap { it.outputDirectory })
        }
        getByName("commonTest") {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        getByName("jvmTest") {
            dependencies {
                implementation(kotlin("reflect"))
                implementation(kotlin("test-junit5"))
                implementation(libs.asm)
                implementation(libs.jetCheck)
                implementation(libs.junit.jupiter.api)
                implementation(libs.kotlinx.metadata.jvm)
                runtimeOnly(libs.junit.jupiter.engine)
            }
        }
        getByName("jsTest") {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

tasks.named<Test>("jvmTest") { useJUnitPlatform() }

val kotlinScripts = files().apply {
    from(fileTree("buildSrc").matching { include("*.kts") })
    allprojects { from(layout.projectDirectory.asFileTree.matching { include("*.kts") }) }
}

tasks.withType<LintTask>().configureEach { exclude("**/*generated*") }
tasks.withType<FormatTask>().configureEach { exclude("**/*generated*") }
tasks.withType<Detekt>().configureEach { exclude("**/*generated*") }

val lintKotlinScripts by tasks.registering(LintTask::class) { source(kotlinScripts) }
tasks.lintKotlin { dependsOn(lintKotlinScripts) }

val formatKotlinScripts by tasks.registering(FormatTask::class) { source(kotlinScripts) }
tasks.formatKotlin { dependsOn(formatKotlinScripts) }

val detektKotlinScripts by tasks.registering(Detekt::class) {
    group = VERIFICATION_GROUP
    description = "Run detekt analysis for Kotlin scripts"
    source(kotlinScripts)
}
tasks.check { dependsOn(detektKotlinScripts) }

val detektAll by tasks.registering {
    group = VERIFICATION_GROUP
    description = "Run all detekt analyses"
    dependsOn(tasks.withType<Detekt>())
}

tasks.dependencyUpdates {
    val unstableVersionPattern = """-(?:m|rc|alpha|beta)(?:[.0-9]|$)""".toRegex(RegexOption.IGNORE_CASE)
    rejectVersionIf {
        unstableVersionPattern.containsMatchIn(candidate.version) &&
            !unstableVersionPattern.containsMatchIn(currentVersion)
    }
}
