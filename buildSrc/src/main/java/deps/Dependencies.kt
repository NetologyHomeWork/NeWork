package deps

object Dependencies {

    object InitialDeps {
        const val coreKtxVersion = "1.10.1"
        const val appCompatVersion = "1.6.1"
        const val materialVersion = "1.9.0"
        const val constraintLayoutVersion = "2.1.4"
        const val junitVersion = "4.13.2"
        const val extJunitVersion = "1.1.5"
        const val espressoVersion = "3.5.1"

        const val coreKtx = "androidx.core:core-ktx:$coreKtxVersion"
        const val appCompat = "androidx.appcompat:appcompat:$appCompatVersion"
        const val material = "com.google.android.material:material:$materialVersion"
        const val constraintlayout = "androidx.constraintlayout:constraintlayout:$constraintLayoutVersion"
        const val junit = "junit:junit:$junitVersion"
        const val extJunit = "androidx.test.ext:junit:$extJunitVersion"
        const val espresso = "androidx.test.espresso:espresso-core:$espressoVersion"
    }

    object RetrofitDeps {
        const val retrofitVersion = "2.9.0"
        const val kotlinSerializationVersion = "0.8.0"

        const val retrofit = "com.squareup.retrofit2:retrofit:$retrofitVersion"
        const val kotlinSerialization = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:$kotlinSerializationVersion"
    }

    object LifecycleDeps {
        const val lifecycleVersion = "2.5.1"

        const val lifecycleRuntimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion"
        const val lifecycleViewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion"
    }

    object HiltDeps {
        const val hiltVersion = "2.44.2"

        const val hilt = "com.google.dagger:hilt-android:$hiltVersion"
        const val hiltCompiler = "com.google.dagger:hilt-compiler:$hiltVersion"
    }

    object OkHttpDeps {
        const val okHttpVersion = "4.10.0"

        const val okHttpBom = "com.squareup.okhttp3:okhttp-bom:$okHttpVersion"
        const val okHttp = "com.squareup.okhttp3:okhttp"
        const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor"
    }

    object CoilDeps {
        const val coilVersion = "2.4.0"

        const val coil = "io.coil-kt:coil:2.4.0"
    }

    object ActivityFragmentKtxDeps {
        const val fragmentVersion = "1.6.0"
        const val activityVersion = "1.7.2"

        const val fragmentKtx = "androidx.fragment:fragment-ktx:$fragmentVersion"
        const val activityKtx = "androidx.activity:activity-ktx:$activityVersion"
    }

    object NavDeps {
        const val navVersion = "2.5.3"

        const val navFragmentKtx = "androidx.navigation:navigation-fragment-ktx:$navVersion"
        const val uiKtx = "androidx.navigation:navigation-ui-ktx:$navVersion"
    }

    object SerializationDeps {
        const val serializationVersion = "1.2.0"

        const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion"
    }

    object ImagePickerDeps {
        const val imagePickerVersion = "2.1"

        const val imagePicker = "com.github.dhaval2404:imagepicker:$imagePickerVersion"
    }

    object SwipeRefreshLayout {
        const val swipeRefreshVersion = "1.1.0"
        const val swipeRefresh = "androidx.swiperefreshlayout:swiperefreshlayout:$swipeRefreshVersion"
    }
}