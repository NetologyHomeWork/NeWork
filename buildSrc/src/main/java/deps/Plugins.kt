package deps

object Plugins {
    const val safeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:${Version.safeArgsVersion}"
    const val application = "com.android.application"
    const val androidlibrary = "com.android.library"
    const val kotlinAndroid = "org.jetbrains.kotlin.android"
    const val daggerHilt = "com.google.dagger.hilt.android"
    const val serialization = "plugin.serialization"
    const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.gradlePluginVersion}"

    object Version {
        const val safeArgsVersion = "2.5.3"
        const val applicationVersion = "7.4.0"
        const val androidlibraryVersion = "7.4.0"
        const val kotlinAndroidVersion = "1.8.21"
        const val daggerHiltVersion = "2.44.2"
        const val serializationVersion = "1.8.21"
        const val gradlePluginVersion = "1.8.21"
    }
}