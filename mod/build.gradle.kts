plugins {
    id("java")
}

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven/")
    maven("https://maven.fabricmc.net/")
}

dependencies {
    implementation("net.fabricmc:fabric-loader:0.18.4")
    implementation("net.fabricmc:sponge-mixin:0.17.0+mixin.0.8.7")
    implementation(annotationProcessor("io.github.llamalad7:mixinextras-fabric:0.5.3")!!)
    compileOnly(files("../libs/HytaleServer.jar"))
}

val javaCompatibility = 21
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaCompatibility))
    }
}