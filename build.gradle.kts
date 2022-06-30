plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.40"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
}

taboolib {
    description {
        dependencies {
            name("Adyeshach")
            name("Zaphkiel")
        }
    }
    install("common")
    install("common-5")
    install("module-configuration")
    install("module-database")
    install("module-lang")
    install("module-ui")
    install("platform-bukkit")
    classifier = null
    version = "6.0.9-10"
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms:Adyeshach:1.5.7")
    compileOnly("ink.ptms:Zaphkiel:1.7.6")

    compileOnly("ink.ptms.core:v11900:11900:mapped")
    compileOnly("ink.ptms.core:v11900:11900:universal")
    compileOnly("ink.ptms:nms-all:1.0.0")

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