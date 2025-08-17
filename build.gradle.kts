plugins {
    kotlin("jvm") version "2.0.0"
}

group = "com.c1ok.bedwars"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss-snapshots"
    }
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly(fileTree("libs"))
    testImplementation(fileTree("libs"))
    compileOnly("net.kyori:adventure-text-minimessage:4.23.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}