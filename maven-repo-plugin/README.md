# Gradle Maven Repository Plugin

Quickly add Maven mirror repositories during Gradle builds, with support for configuring private repositories through environment variables.

1. [Gradle - Plugin: io.github.bitlumen.gradle.maven-repo-plugin](https://plugins.gradle.org/plugin/io.github.bitlumen.gradle.maven-repo-plugin)
2. [gradle-plugins/maven-repo-plugin at main · bitlumen/gradle-plugins](https://github.com/bitlumen/gradle-plugins/tree/main/maven-repo-plugin)

## Prerequisites

Before using the plugin, you must define an **environment variable prefix (envPrefix)**. The plugin will then construct the following
variable names by appending suffixes to this prefix:

- Repository URL: `${envPrefix}_url`
- Authentication username: `${envPrefix}_username`
- Authentication password: `${envPrefix}_password`

If you need to **publish artifacts** and want to distinguish between `snapshot` and `release` repositories, you must also configure these
additional variables:

- Snapshot repository URL: `${envPrefix}_snapshots`
- Release repository URL: `${envPrefix}_releases`

Each variable is resolved in the following priority order:

1. **System environment variable**
2. **`gradle.properties`** (project directory takes precedence over user home `~/.gradle/gradle.properties`)

### Example

If the `envPrefix` is set to `private_maven_repo`, you can configure via environment variables:

```
private_maven_repo_url=https://xxx.com/repository
private_maven_repo_username=<username>
private_maven_repo_password=<password>
private_maven_repo_snapshots=https://xxx.com/repository/snapshots
private_maven_repo_releases=https://xxx.com/repository/releases
```

Or via `gradle.properties` (project directory or `~/.gradle/gradle.properties`):

```properties
private_maven_repo_url=https://xxx.com/repository
private_maven_repo_username=<username>
private_maven_repo_password=<password>
private_maven_repo_snapshots=https://xxx.com/repository/snapshots
private_maven_repo_releases=https://xxx.com/repository/releases
```

## Usage

### 1. Config maven mirror

Configure the plugin in **settings.gradle.kts** and **build.gradle.kts** as follows:

```kotlin
plugins {
    id("io.github.bitlumen.gradle.maven-repo-plugin") version "<versionNum>"
}

// project/settings.gradle.kts
pluginManagement {
    repositories {
        mavenAliyunPlugin()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenAliyun()
        mavenAuth("private_maven_repo", project)
        mavenPublic("public_maven_repo", project)
    }
}

// project/app/build.gradle.kts
repositories {
    mavenAliyun()
    mavenAuth("private_maven_repo", project)
    mavenAuth("private_maven_repo2", project)
    mavenPublic("public_maven_repo", project)
    mavenPublic("public_maven_repo2", project)
}

publishing {
    repositories {
        // Use a single repository for both snapshot and release
        mavenAuth("private_maven_repo", project)
        // Support snapshot and release repositories separately
        mavenPublish("private_maven_repo", version.toString().endsWith("SNAPSHOT"), project)
    }
}
```

### 2. Convert plugin to GAV String

The `pluginGAV` method provide convenient way to convert Gradle plugin into the standard Maven GAV (Group:Artifact:Version) format for
usage in precompiled script plugins, see [Implementing Pre-compiled Script Plugins](https://docs.gradle.org/current/userguide/implementing_gradle_plugins_precompiled.html#sec:applying_external_plugins).

```kotlin
plugins {
    `kotlin-dsl`
    id("io.github.bitlumen.gradle.maven-repo-plugin") version "1.0.0"
}

dependencies {
    // usage 1：using Version Catalogs
    implementation(pluginGAV(libs.plugins.maven.repo.plugin))
    // usage2: or using pluginId and version
    implementation(pluginGAV("io.github.bitlumen.gradle.maven-repo-plugin", "1.0.0"))
    // usage3: or using GAV (Group:Artifact:Version) format directly
    implementation("io.github.bitlumen.gradle.maven-repo-plugin:io.github.bitlumen.gradle.maven-repo-plugin.gradle.plugin:1.0.0")
}
```
