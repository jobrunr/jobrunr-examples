plugins {
    kotlin("jvm") version "2.3.20"
    kotlin("plugin.serialization") version "2.3.21"
}

group = "org.jobrunr"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jobrunr:jobrunr:8.6.0")
    implementation("org.jobrunr:jobrunr-kotlin-support:8.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
    implementation("ch.qos.logback:logback-classic:1.5.32")
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}