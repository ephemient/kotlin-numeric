package com.github.ephemient.kotlin.numeric.annotations

@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class CheckedArithmetic(val mode: Mode = Mode.CHECKED) {
    enum class Mode {
        UNCHECKED,
        CHECKED,
    }
}
