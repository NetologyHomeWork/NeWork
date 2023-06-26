import deps.DefaultConfigs
import deps.Dependencies

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlinx-serialization")
    id("androidx.navigation.safeargs.kotlin")
}

@Suppress("UnstableApiUsage")
android {
    namespace = "ru.netology.nework"
    compileSdk = 33

    defaultConfig {
        applicationId = DefaultConfigs.applicationId
        minSdk = DefaultConfigs.minSdk
        targetSdk = DefaultConfigs.targetSdk
        versionCode = DefaultConfigs.versionCode
        versionName = DefaultConfigs.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", DefaultConfigs.baseUrl, DefaultConfigs.baseUrlValue)
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", DefaultConfigs.baseUrl, DefaultConfigs.baseUrlValue)
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {

    implementation(Dependencies.InitialDeps.coreKtx)
    implementation(Dependencies.InitialDeps.appCompat)
    implementation(Dependencies.InitialDeps.material)
    implementation(Dependencies.InitialDeps.constraintlayout)
    testImplementation(Dependencies.InitialDeps.junit)
    androidTestImplementation(Dependencies.InitialDeps.extJunit)
    androidTestImplementation(Dependencies.InitialDeps.espresso)

    implementation(Dependencies.RetrofitDeps.retrofit)
    implementation(Dependencies.RetrofitDeps.kotlinSerialization)

    implementation(Dependencies.LifecycleDeps.lifecycleRuntimeKtx)
    implementation(Dependencies.LifecycleDeps.lifecycleViewModelKtx)

    implementation(Dependencies.HiltDeps.hilt)
    kapt(Dependencies.HiltDeps.hiltCompiler)

    implementation(platform(Dependencies.OkHttpDeps.okHttpBom))
    implementation(Dependencies.OkHttpDeps.okHttp)
    implementation(Dependencies.OkHttpDeps.loggingInterceptor)

    implementation(Dependencies.CoilDeps.coil)

    implementation(Dependencies.ActivityFragmentKtxDeps.fragmentKtx)
    implementation(Dependencies.ActivityFragmentKtxDeps.activityKtx)

    implementation(Dependencies.NavDeps.navFragmentKtx)
    implementation(Dependencies.NavDeps.uiKtx)
}