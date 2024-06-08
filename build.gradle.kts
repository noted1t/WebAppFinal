plugins {
    alias(libs.plugins.kotlin.jvm).apply(false)
    alias(libs.plugins.ktor.server).apply(false)
    alias(libs.plugins.multiplatform).apply(false)
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.compose).apply(false)
    alias(libs.plugins.kotlinx.serialization).apply(false)
}
