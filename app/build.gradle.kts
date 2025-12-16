plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "1.9.23"
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.shin.vicmusic"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.shin.vicmusic"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.navigation.compose)

    // 新增图标库依赖
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended) // 可选，要Outlined风格就加

    //kotlin序列化
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    //hilt
    implementation("com.google.dagger:hilt-android:2.57.1")
    ksp("com.google.dagger:hilt-android-compiler:2.57.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    //网络框架
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    //网络日志
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    //类型安全网络
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    //让retrofit支持kotlin序列化
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

    //图片加载框架
    implementation("io.coil-kt:coil-compose:2.6.0")

    // AndroidX Media3 (推荐使用，它包含ExoPlayer)
    implementation("androidx.media3:media3-exoplayer:1.2.1") // 检查最新版本
    implementation("androidx.media3:media3-ui:1.2.1") // 如果需要 Media3 提供的 UI 组件

    implementation("com.google.accompanist:accompanist-flowlayout:0.28.0") // 检查最新版本

    //数据存储
    implementation("androidx.datastore:datastore-preferences:1.0.0")

}