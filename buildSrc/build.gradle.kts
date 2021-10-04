plugins {
    `embedded-kotlin`
    alias(libs.plugins.detekt)
}

kotlin.target.compilations.all {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.ExperimentalUnsignedTypes"
}

dependencies {
    implementation(libs.kotlinpoet)
}
