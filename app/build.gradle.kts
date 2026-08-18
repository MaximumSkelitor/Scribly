import com.android.build.api.variant.impl.VariantOutputImpl
import org.gradle.kotlin.dsl.assign

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinCompose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sentry)
    id("com.google.gms.google-services") version "4.4.2"
    id("kotlin-parcelize")
}

val vMajor = 1
val vMinor = 0
val vPatch = 0
val isAlpha = true

android {
    namespace = "com.weberpackage.scribly"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.weberpackage.scribly"
        minSdk = 24
        targetSdk = 37
        versionCode = vMajor * 1000000 + vMinor * 10000 + vPatch * 100
        versionName = "${vMajor}.${vMinor}.${vPatch}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            buildConfigField("String", "BUILD_TIME", "\"${getCurrentTime()}\"")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders.putAll(mapOf("appName" to "Scribly"))
            buildConfigField("Boolean", "ALPHA_BUILD", isAlpha.toString())
        }
        debug {
            buildConfigField("String", "BUILD_TIME", "\"${getCurrentTime()}\"")
            applicationIdSuffix = ".debug"
            manifestPlaceholders.putAll(mapOf("appName" to "Scribly Debug"))
            buildConfigField("Boolean", "ALPHA_BUILD", isAlpha.toString())
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

sentry {
    includeProguardMapping.set(true)
    autoUploadProguardMapping.set(true)
    uploadNativeSymbols.set(false)
    includeNativeSources.set(false)
}

androidComponents {
    onVariants { variant ->
        val appName = rootProject.name.lowercase()
        val buildType = variant.buildType
        variant.outputs.forEach {
            val apkName = "${appName}-${buildType}-${it.versionName.get()}.apk"
            (it as VariantOutputImpl).outputFileName = apkName
        }
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.haze)
    implementation(libs.haze.materials)
    implementation(libs.tapadoo.alerter)
    implementation(libs.composables.core)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.glance)
    implementation(libs.androidx.glance.material3)
    implementation(libs.sentry.android)
    implementation(libs.sentry.compose)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.config)
    implementation(libs.firebase.analytics)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

fun getCurrentTime(): String {
    return System.currentTimeMillis().toString()
}