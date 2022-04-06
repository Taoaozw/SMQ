plugins {
    kotlin("jvm") version "1.6.20-RC2"
    kotlin("plugin.serialization") version "1.6.20-RC2"
}

group = "io.github.suda"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(libs.kt.log)
    implementation(libs.kotlin.coroutine)
    implementation(libs.bundles.vertxMq)
    implementation(libs.bundles.logback)
    implementation(libs.bundles.jackson)
    api(libs.bundles.ktoml)
    implementation(kotlin("stdlib"))
    implementation("com.squareup.okio:okio:3.0.0")
}