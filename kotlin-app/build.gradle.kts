plugins {
    kotlin("jvm") version "2.4.20"
    kotlin("plugin.serialization") version "2.4.20"
    application
}

group = "org.jobrunr"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("org.jobrunr.example.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jobrunr:jobrunr:9.0.0")
    implementation("org.jobrunr:jobrunr-kotlin-support:9.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
    implementation("ch.qos.logback:logback-classic:1.6.5")

    testImplementation("org.jobrunr:jobrunr:9.0.0:test-fixtures")

    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.27.7")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}
