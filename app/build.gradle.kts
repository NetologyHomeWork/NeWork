import com.android.build.api.dsl.VariantDimension

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.androidx.navigation.safe.args)
    alias(libs.plugins.google.dagger.hilt.android)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.jetbrains.kotlin.plugin.serialization)
    id("kotlin-parcelize")
}

android {
    namespace = libs.versions.applicationId.get()
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = libs.versions.applicationId.get()
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField(getProperty(PropertyValues.BASE_URL), getProperty(PropertyValues.BASE_URL_VALUE))
            buildConfigField(getProperty(PropertyValues.IS_LOG_ENABLED), false)
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField(getProperty(PropertyValues.BASE_URL), getProperty(PropertyValues.BASE_URL_VALUE))
            buildConfigField(getProperty(PropertyValues.IS_LOG_ENABLED), false)
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.android.material)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.bundles.retrofit)

    implementation(libs.bundles.lifecycle)

    implementation(libs.google.dagger.hilt.android)
    ksp(libs.google.dagger.hilt.compiler)

    implementation(libs.bundles.okHttp3)

    implementation(libs.coil.kt)

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)

    implementation(libs.bundles.navigation)

    implementation(libs.jetbrains.kotlinx.serialization.json)
    implementation(libs.github.dhaval2404.imagepicker)
    implementation(libs.androidx.swiperefreshlayout)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

inline fun <reified T> VariantDimension.buildConfigField(
    name: String,
    value: T
) {
    @Suppress("IMPLICIT_CAST_TO_ANY")
    val resolveValue = when (value) {
        is String -> "\"$value\""
        else -> value
    }.toString()
    buildConfigField(T::class.java.simpleName, name, resolveValue)
}

fun getProperty(propertyName: String): String {
    return project.properties[propertyName].toString()
}

object PropertyValues {
    const val BASE_URL = "BASE_URL"
    const val BASE_URL_VALUE = "BASE_URL_VALUE"
    const val IS_LOG_ENABLED = "IS_LOG_ENABLED"
}