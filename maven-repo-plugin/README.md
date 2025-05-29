# Gradle Maven Repository Plugin

Quickly add Maven mirror repositories during Gradle builds, with support for configuring private repositories through environment variables.

## Prerequisites

Before using the plugin, you must define an **environment variable prefix (envPrefix)**. The plugin will then construct the following 
environment variables by appending suffixes to this prefix:

- Repository URL: `${envPrefix}_url`
- Authentication username: `${envPrefix}_username`
- Authentication password: `${envPrefix}_password`

If you need to **publish artifacts** and want to distinguish between `snapshot` and `release` repositories, you must also configure these 
additional environment variables:

- Snapshot repository URL: `${envPrefix}_snapshots`
- Release repository URL: `${envPrefix}_releases`

### Example

If the `envPrefix` is set to `private_maven_repo`, the required environment variables would be:

```
private_maven_repo_url = "https://xxx.com/repository"
private_maven_repo_username = <username>
private_maven_repo_password = <password>
private_maven_repo_snapshots = "https://xxx.com/repository/snapshots"
private_maven_repo_releases = "https://xxx.com/repository/releases"
```

## Usage

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
        mavenEnvAuth("private_maven_repo")
        mavenEnvPublic("public_maven_repo")
    }
}


// project/app/build.gradle.kts
repositories {
    mavenAliyun()
    mavenEnvAuth("private_maven_repo")
    mavenEnvAuth("private_maven_repo2")
    mavenEnvPublic("public_maven_repo")
    mavenEnvPublic("public_maven_repo2")
}

publishing {
    repositories {
        // Use a single repository for both snapshot and release
        mavenEnvAuth("private_maven_repo")
        // Support snapshot and release repositories separately
        mavenPublishEnv("private_maven_repo", version.toString().endsWith("SNAPSHOT"))
    }
}
```
