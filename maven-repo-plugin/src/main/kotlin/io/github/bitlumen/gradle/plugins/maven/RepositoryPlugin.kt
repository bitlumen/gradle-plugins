package io.github.bitlumen.gradle.plugins.maven

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.api.provider.Provider
import org.gradle.plugin.use.PluginDependency
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
 * 通过环境变量或gradle.properties配置的Maven仓库配置（需要认证）
 *
 * 通过拼接获取下列变量名称对应的信息（优先从环境变量获取，其次从gradle.properties获取）：
 * 1. 仓库地址：${envPrefix}_url
 * 2. 认证账号：${envPrefix}_username
 * 3. 认证密码：${envPrefix}_password
 *
 * @param envPrefix 变量名称前缀
 * @param project Gradle项目对象，用于读取gradle.properties属性
 */
fun RepositoryHandler.mavenAuth(envPrefix: String, project: Project) {
    maven {
        isAllowInsecureProtocol = true
        url = URI(getConfig("${envPrefix}_url", project))
        credentials {
            username = getConfig("${envPrefix}_username", project)
            password = getConfig("${envPrefix}_password", project)
        }
    }
}

/**
 * 通过环境变量或gradle.properties配置的Maven仓库配置（公开的仓库，不需要认证）
 *
 * 通过拼接获取变量 ${envPrefix}_url 对应的仓库地址（优先从环境变量获取，其次从gradle.properties获取）
 *
 * @param envPrefix 变量名称前缀
 * @param project Gradle项目对象，用于读取gradle.properties属性
 */
fun RepositoryHandler.mavenPublic(envPrefix: String, project: Project) {
    maven {
        isAllowInsecureProtocol = true
        url = URI(getConfig("${envPrefix}_url", project))
    }
}

/**
 * 通过环境变量或gradle.properties配置的用于发布构件的Maven仓库配置（需要认证）
 *
 * 通过拼接获取下列变量名称对应的信息（优先从环境变量获取，其次从gradle.properties获取）：
 * 1. 快照仓库地址：${envPrefix}_snapshots
 * 2. 发布仓库地址：${envPrefix}_releases
 * 3. 认证账号：${envPrefix}_username
 * 4. 认证密码：${envPrefix}_password
 *
 * @param envPrefix 变量名称前缀
 * @param isSnapshot 是否是快照
 * @param project Gradle项目对象，用于读取gradle.properties属性
 */
fun RepositoryHandler.mavenPublish(envPrefix: String, isSnapshot: Boolean, project: Project) {
    maven {
        isAllowInsecureProtocol = true
        val snapshotsUrl = getConfig("${envPrefix}_snapshots", project)
        val releasesUrl = getConfig("${envPrefix}_releases", project)
        url = URI(if (isSnapshot) snapshotsUrl else releasesUrl)
        credentials {
            username = getConfig("${envPrefix}_username", project)
            password = getConfig("${envPrefix}_password", project)
        }
    }
}

/**
 * 获取配置值，优先从环境变量获取，其次从gradle.properties获取
 *
 * 查找优先级：
 * 1. 系统环境变量
 * 2. gradle.properties（项目目录 > 用户目录 ~/.gradle/gradle.properties）
 *
 * @param name 变量名称
 * @param project Gradle项目对象
 * @return 配置值
 * @throws IllegalStateException 当环境变量和属性文件中均不存在时抛出
 */
private fun getConfig(name: String, project: Project): String {
    return System.getenv(name)
        ?: (project.findProperty(name) as? String)
        ?: throw IllegalStateException("Configuration 【$name】 not set!")
}

/**
 * 将插件转换为Gradle插件依赖的GAV格式
 *
 * @param plugin 插件
 * @return 格式为 "pluginId:pluginId.gradle.plugin:version" 的 GAV 字符串
 */
fun DependencyHandlerScope.pluginGAV(plugin: Provider<PluginDependency>) = plugin.map { pluginGAV(it.pluginId, it.version.toString()) }

/**
 * 将插件ID和版本转换为Gradle插件依赖的GAV格式
 * @param pluginId 插件ID (如 "com.example")
 * @param version 插件版本 (如 "1.0.0")
 * @return 格式为 "pluginId:pluginId.gradle.plugin:version" 的 GAV 字符串
 */
fun DependencyHandlerScope.pluginGAV(pluginId: String, version: String) = "${pluginId}:${pluginId}.gradle.plugin:${version}"
