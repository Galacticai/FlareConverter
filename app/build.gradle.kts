plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.galacticai.flareconverter"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.galacticai.flareconverter"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "0.1-beta"

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
    val javaVersion = JavaVersion.VERSION_17
    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    kotlinOptions {
        jvmTarget = javaVersion.toString()
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.ui.android)
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.datastore.preferences)
    //implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.androidx.junit.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.ffmpeg.kit.full)
}