package io.github.bitlumen.gradle.plugins.maven

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.Settings
import java.net.URI

/**
 * Gradle Maven Repository Plugin
 *
 * @author bitlumen
 * @date 2025/05/28 18:17
 */
class RepositoryPlugin : Plugin<Any> {

    override fun apply(target: Any) {
        when {
            target is Project || target is Settings -> {
                // nothing to do
            }
            else -> throw IllegalArgumentException(
                "Unsupported target type: ${target::class.java.name}. " + "Only Project and Settings are supported."
            )
        }
    }
}

/**
 * 阿里云Maven仓库配置
 */
fun RepositoryHandler.mavenAliyun() {
    maven { url = URI("https://maven.aliyun.com/repository/public") }
}

/**
 * 阿里云Gradle插件仓库配置
 */
fun RepositoryHandler.mavenAliyunPlugin() {
    maven { url = URI("https://maven.aliyun.com/repository/gradle-plugin") }
}

/**
 * 通过环境变量配置的Maven仓库配置（需要认证）
 *
 * 通过拼接获取下列环境变量名称对应的信息：
 * 1. 仓库地址：${envPrefix}_url
 * 2. 认证账号：${envPrefix}_username
 * 3. 认证密码：${envPrefix}_password
 *
 * @param envPrefix 环境变量名称前缀
 */
fun RepositoryHandler.mavenEnvAuth(envPrefix: String) {
    maven {
        isAllowInsecureProtocol = true
        url = URI(getEnv("${envPrefix}_url"))
        credentials {
            username = getEnv("${envPrefix}_username")
            password = getEnv("${envPrefix}_password")
        }
    }
}

/**
 * 通过环境变量配置的Maven仓库配置（公开的仓库，不需要认证）
 *
 * 通过拼接获取环境变量 ${envPrefix}_url 对应的仓库地址
 *
 * @param envPrefix 环境变量名称前缀
 */
fun RepositoryHandler.mavenEnvPublic(envPrefix: String) {
    maven {
        isAllowInsecureProtocol = true
        url = URI(getEnv("${envPrefix}_url"))
    }
}

/**
 * 通过环境变量配置的用于发布构件的Maven仓库配置（需要认证）
 *
 * 通过拼接获取下列环境变量名称对应的信息：
 * 1. 快照仓库地址：${envPrefix}_snapshots
 * 2. 发布仓库地址：${envPrefix}_releases
 * 3. 认证账号：${envPrefix}_username
 * 4. 认证密码：${envPrefix}_password
 *
 * @param envPrefix 环境变量名称前缀
 * @param isSnapshot 是否是快照
 */
fun RepositoryHandler.mavenPublishEnv(envPrefix: String, isSnapshot: Boolean) {
    maven {
        isAllowInsecureProtocol = true
        val snapshotsUrl = getEnv("${envPrefix}_snapshots")
        val releasesUrl = getEnv("${envPrefix}_releases")
        url = URI(if (isSnapshot) snapshotsUrl else releasesUrl)
        credentials {
            username = getEnv("${envPrefix}_username")
            password = getEnv("${envPrefix}_password")
        }
    }
}

/**
 * 通过环境变量名称获取对应的值
 *
 * @param envName 环境变量名称
 * @return 环境变量值
 * @throws IllegalStateException 当环境变量不存在时抛出
 */
private fun getEnv(envName: String): String {
    return System.getenv(envName) ?: throw IllegalStateException("Environment 【$envName】 is not set!")
}
