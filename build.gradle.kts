plugins {
    kotlin("jvm") version "1.6.20-RC2"
}

group = "clive.tea.milk"
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
    implementation(kotlin("stdlib"))
}