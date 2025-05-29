plugins {
    `kotlin-dsl`
    alias(libs.plugins.plugin.publish)
}

version = "1.0.0"
description = "Gradle Maven Repository Plugin for Gradle Build"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
    withJavadocJar()
}

gradlePlugin {
    website = "https://github.com/bitlumen/gradle-plugins"
    vcsUrl = "https://github.com/bitlumen/gradle-plugins/tree/main/maven-repo-plugin"
    plugins {
        create("mavenRepoPlugin") {
            id = "io.github.bitlumen.gradle.maven-repo-plugin"
            implementationClass = "io.github.bitlumen.gradle.plugins.maven.RepositoryPlugin"
            displayName = "Gradle Maven Repository Plugin"
            description = project.description
            tags.set(listOf("build", "maven", "repository"))
        }
    }
}

publishing {
    repositories {
        maven {
            name = "testRepository"
            url = uri("${gradle.gradleUserHomeDir}/test-repository")
        }
    }
}
