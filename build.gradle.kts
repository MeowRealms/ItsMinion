plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.56"
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
}

val adyeshachVersion = "2.0.0-snapshot-25"

taboolib {
    description {
        contributors {
            name("ItsFlicker")
        }
    }
    install("common")
    install("common-5")
    install("module-chat")
    install("module-configuration")
//    install("module-database")
    install("module-effect")
    install("module-kether")
    install("module-lang")
    install("module-metrics")
    install("module-nms")
    install("module-nms-util")
    install("module-navigation")
    install("module-ui")
    install("platform-bukkit")
    install("expansion-command-helper")
    install("expansion-ioc")
    classifier = null
    version = "6.0.12-40"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly("com.google.code.gson:gson:2.8.5")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")

    compileOnly("ink.ptms.core:v12002:12002:mapped")
    compileOnly("ink.ptms.core:v12002:12002:universal")
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("net.md-5:bungeecord-chat:1.17")

    taboo("ink.ptms.adyeshach:common:$adyeshachVersion")
    taboo("ink.ptms.adyeshach:common-impl:$adyeshachVersion")
    taboo("ink.ptms.adyeshach:common-impl-nms:$adyeshachVersion")
    taboo("ink.ptms.adyeshach:common-impl-nms-j17:$adyeshachVersion")
    taboo("ink.ptms.adyeshach:module-language:$adyeshachVersion")
    taboo("ink.ptms.adyeshach:api-data-serializer:$adyeshachVersion")
    compileOnly("ink.ptms:Zaphkiel:2.0.14")

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

publishing {
    repositories {
        maven {
            url = uri("https://repo.tabooproject.org/repository/releases")
            credentials {
                username = project.findProperty("taboolibUsername").toString()
                password = project.findProperty("taboolibPassword").toString()
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            groupId = project.group.toString()
        }
    }
}