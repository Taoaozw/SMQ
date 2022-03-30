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
    implementation(kotlin("stdlib"))
}