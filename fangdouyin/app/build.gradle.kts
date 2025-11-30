plugins {
    alias(libs.plugins.android.application)
    // Kotlin serialization plugin for type safe routes and navigation arguments
    kotlin("plugin.serialization") version "2.0.21"
    alias(libs.plugins.jetbrains.kotlin.kapt)
    id("kotlin-kapt")

}

android {
    namespace = "com.example.fangdouyin"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.fangdouyin"
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    val nav_version = "2.9.6"

    // Jetpack Compose integration
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // Views/Fragments integration
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")

    // Feature module support for Fragments
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")

    // JSON serialization library, works with the Kotlin serialization plugin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // 网络请求（Retrofit + OkHttp + Gson）
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    // 图片加载（Glide）
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.16.0")


    //下拉刷新组件
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Room
    implementation ("androidx.room:room-runtime:2.5.2")
    annotationProcessor ("androidx.room:room-compiler:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")






}


