import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.plugin)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.gms)
    alias(libs.plugins.ksp)
}


val secretsFile = Properties().apply {
    load(rootProject.file("secrets.properties").inputStream())
}

android {
    namespace = "com.arakene.fillsa"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.arakene.fillsa"
        minSdk = 28
        targetSdk = 35
        versionCode = 3
        versionName = "1.0.7"
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = secretsFile["KAKAO_KEY"].toString()

        manifestPlaceholders["appAuthRedirectScheme"] = "com.arakene.fillsa"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    apply(from = "../common.gradle")
    apply(from = "../signing_config.gradle")
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":presentation"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.common.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.kakao.login)

    implementation(libs.hilt)
    kapt(libs.hilt.compiler)
    kapt(libs.hilt.work)
    kapt(libs.hilt.androidx.compiler)
    implementation(libs.hilt.work)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.logger)
    implementation(libs.datastore)
    implementation(platform(libs.firebase.bom))

    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
}