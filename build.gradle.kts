import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val ktorVersion = "3.3.1"

plugins {
    application
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
    id("com.gradleup.shadow") version "9.2.2"
}

group = "de.modulescanner"
version = "1.1"
application {
    mainClass.set("io.ktor.server.cio.EngineMain")
}

tasks.test {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
    maven {
        group
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/Scui1/KotlinPEFile")
        credentials {
            username = System.getenv("GITHUB_USERNAME")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("de.scui:kotlin-pefile:1.4.0")
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cio-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.5.19")
    testImplementation(kotlin("test"))
}

tasks.withType<ShadowJar> {
    archiveFileName.set("modulescanner.jar")
}