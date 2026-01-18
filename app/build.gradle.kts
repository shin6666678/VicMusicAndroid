plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp) // 使用 Version Catalog 管理 KSP
    alias(libs.plugins.hilt) // 使用 Version Catalog 管理 Hilt
    alias(libs.plugins.kotlin.serialization) // 使用 Version Catalog 管理 Serialization
}

android {
    namespace = "com.shin.vicmusic"
    compileSdk = 36 // 建议使用 35 (Android 15) 作为当前稳定编译目标，36 也可以但可能处于预览阶段

    defaultConfig {
        applicationId = "com.shin.vicmusic"
        minSdk = 24
        targetSdk = 36 // 与 compileSdk 匹配
        versionCode = 6
        versionName = "1.0.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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

    // AGP 8+ 必须使用 Java 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // AndroidX Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    // 图标库
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt (DI)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler) // 使用 KSP 替代 kapt
    implementation(libs.androidx.hilt.navigation.compose)

    // Network (Retrofit, OkHttp, Serialization)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.gson) // 如果你还在用 GSON，建议逐步迁移到 Kotlinx Serialization

    // Image Loading
    implementation(libs.coil.compose)

    // Media3 (ExoPlayer)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.session)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Accompanist (注意：这些库已大部分废弃，建议未来迁移)
    implementation(libs.accompanist.flowlayout) // 推荐迁移至 Compose Foundation 的 FlowRow
    implementation(libs.accompanist.systemuicontroller) // 推荐迁移至 Activity.enableEdgeToEdge
    implementation(libs.accompanist.drawablepainter)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation("com.google.zxing:core:3.5.3")
}