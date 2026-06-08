plugins {
    kotlin("jvm") version "2.3.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("org.example.SokobanApp")
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}