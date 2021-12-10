plugins {
    kotlin("jvm")
}

val agent by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

dependencies {
    agent(projects.agent) {
        capabilities {
            requireCapability("com.github.ephemient.kotlin.numeric:agent:1")
        }
    }
    implementation(projects.annotations)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.test {
    inputs.files(agent)
    useJUnitPlatform()
    jvmArgumentProviders.add { listOf("-javaagent:${agent.resolve().single()}=") }
}
