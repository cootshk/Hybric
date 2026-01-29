plugins {
    kotlin("jvm") version "2.3.0"
    id("com.gradleup.shadow") version "9.3.1"
}

group = "dev.cootshk"
version = "0.1.0"

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://repo.spongepowered.org/maven/")
    maven("https://maven.fabricmc.net/")
}

dependencies {
    implementation("net.fabricmc:fabric-loader:0.18.4")
    implementation("net.fabricmc:access-widener:2.1.0")
    implementation("net.fabricmc:tiny-mappings-parser:0.2.2.14")
    implementation("com.google.guava:guava:33.5.0-jre")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("org.ow2.asm:asm:9.9.1")
    implementation("org.ow2.asm:asm-analysis:9.9.1")
    implementation("org.ow2.asm:asm-commons:9.9.1")
    implementation("org.ow2.asm:asm-tree:9.9.1")
    implementation("org.ow2.asm:asm-util:9.9.1")
    implementation("net.fabricmc:sponge-mixin:0.17.0+mixin.0.8.7")
    implementation("io.github.llamalad7:mixinextras-fabric:0.5.3")
    compileOnly(files("libs/HytaleServer.jar"))
}

val javaCompatibility = 21
kotlin {
    jvmToolchain(javaCompatibility)
}
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaCompatibility))
    }
}

tasks.shadowJar {
    manifest {
        attributes(mapOf(
            "Class-Path" to "HytaleServer.jar",
            "Main-Class" to "net.fabricmc.loader.impl.launch.knot.KnotServer"
        ))
    }
}