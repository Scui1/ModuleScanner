import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val ktorVersion = "2.2.4"

plugins {
    application
    kotlin("jvm") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "de.modulescanner"
version = "1.0"
application {
    mainClass.set("io.ktor.server.cio.EngineMain")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cio-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.4.6")
}

tasks.withType<ShadowJar> {
    archiveFileName.set("modulescanner.jar")
}