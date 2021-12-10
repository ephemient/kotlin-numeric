plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(8))
    }
}

val r8 by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

dependencies {
    implementation(projects.annotations)
    implementation(libs.asm)
    r8(libs.r8)
}

tasks.jar {
    manifest.attributes(
        "Premain-Class" to "com.github.ephemient.kotlin.numeric.agent.Agent",
        "Agent-Class" to "com.github.ephemient.kotlin.numeric.agent.Agent",
        "Can-Redefine-Classes" to false,
        "Can-Retransform-Classes" to false
    )
}

val fatJar by tasks.registering(Jar::class) {
    inputs.files(configurations.runtimeClasspath, tasks.jar)
    archiveClassifier.set("fat")
    doFirst {
        manifest.attributes(tasks.jar.get().manifest.attributes)
        configurations.runtimeClasspath.get().asFileTree.forEach { from(zipTree(it)) }
        from(zipTree(tasks.jar.get().archiveFile))
    }
    exclude("**/*.kotlin_metadata")
    exclude("**/*.kotlin_module")
    exclude("**/*.kotlin_builtins")
    exclude("**/module-info.class")
    exclude("META-INF/maven/**")
}

val shadowJar by tasks.registering(JavaExec::class) {
    javaLauncher.set(
        javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(8))
        }
    )
    inputs.files(r8, "proguard-rules.txt", fatJar)
    outputs.files(layout.buildDirectory.file("libs/${project.name}-shadow.jar"))
    classpath(r8)
    mainClass.set("com.android.tools.r8.R8")
    args("--release", "--classfile")
    argumentProviders.add { listOf("--output", outputs.files.singleFile.toString()) }
    argumentProviders.add { listOf("--pg-conf", file("proguard-rules.txt").toString()) }
    argumentProviders.add { (listOf("--lib", javaLauncher.get().metadata.installationPath.toString())) }
    argumentProviders.add { listOf(fatJar.get().archiveFile.get().asFile.toString()) }
}

val shadowJarTest by tasks.registering {
    inputs.files(shadowJar)
    doLast {
        val jar = shadowJar.get().outputs.files.singleFile
        val badPaths = mutableListOf<String>()
        zipTree(jar).matching {
            exclude("META-INF/**")
            exclude("com/github/ephemient/kotlin/numeric/agent/**")
        }.visit { badPaths.add(path) }
        if (badPaths.isNotEmpty()) throw GradleException("$jar contained extranous paths: $badPaths")
    }
}

val shadow by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
        attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.SHADOWED))
        attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
        attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, objects.named(TargetJvmEnvironment.STANDARD_JVM))
    }
    outgoing {
        capability("com.github.ephemient.kotlin.numeric:agent:1")
    }
}

artifacts {
    add(shadow.name, shadowJar) {
        classifier = "shadow"
    }
}

tasks.check {
    dependsOn(shadowJarTest)
}
