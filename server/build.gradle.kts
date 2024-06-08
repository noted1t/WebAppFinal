plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor.server)
    alias(libs.plugins.kotlinx.serialization)
}

application {
    val isDevel = true
    mainClass.set("com.doma.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${isDevel}")
}

dependencies {
    implementation(libs.bundles.ktor.server)
    implementation(libs.bundles.exposed.server)
    implementation(libs.h2db)
    implementation(project(":shared"))
}
