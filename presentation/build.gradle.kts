import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.plugin)
    alias(libs.plugins.kotlinx.serialization.json)
}

val secretsFile = Properties().apply {
    load(rootProject.file("secrets.properties").inputStream())
}


android {
    namespace = "com.arakene.presentation"
    compileSdk = 35

    defaultConfig {
        minSdk = 28
        targetSdk = 35

        buildConfigField("String", "kakao_key", secretsFile["KAKAO_KEY"].toString())
        buildConfigField("String", "google_key", secretsFile["GOOGLE_KEY"].toString())

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
    apply(from = "../common.gradle")
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.kakao.login)
    implementation(libs.kakao.share)
    implementation(libs.google.login)
    implementation(libs.google.login.auth)
    implementation(libs.googleid)
    implementation(libs.appauth)
    implementation(libs.authtest)

    implementation(libs.hilt)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation)
    runtimeOnly(libs.material3)
    implementation(libs.compose.navigation)
}