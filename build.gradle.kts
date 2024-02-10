import io.izzel.taboolib.gradle.*

plugins {
    `java-library`
    id("io.izzel.taboolib") version "2.0.5"
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
}

taboolib {
    description {
        contributors {
            name("ItsFlicker")
        }
        dependencies {
            name("Adyeshach")
            name("Zaphkiel")
        }
    }
    env {
        install(UNIVERSAL, UI, EXPANSION_SUBMIT_CHAIN, EXPANSION_IOC, BUKKIT_ALL)
    }
    version {
        taboolib = "6.1.0"
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

val adyeshachVersion = "2.0.0-snapshot-36"

dependencies {
    compileOnly("ink.ptms.core:v12004:12004:mapped")
    compileOnly("ink.ptms.core:v12004:12004:universal")
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("net.md-5:bungeecord-chat:1.17")

    compileOnly("ink.ptms.adyeshach:common:$adyeshachVersion")
    compileOnly("ink.ptms.adyeshach:common-impl:$adyeshachVersion")
    compileOnly("ink.ptms.adyeshach:common-impl-nms:$adyeshachVersion")
    compileOnly("ink.ptms.adyeshach:common-impl-nms-j17:$adyeshachVersion")
    compileOnly("ink.ptms.adyeshach:module-language:$adyeshachVersion")
    compileOnly("ink.ptms.adyeshach:api-data-serializer:$adyeshachVersion")
    compileOnly("ink.ptms:Zaphkiel:2.0.14")

    compileOnly("com.google.code.gson:gson:2.8.5")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}