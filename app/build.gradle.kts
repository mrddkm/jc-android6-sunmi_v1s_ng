import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.kotlin.parcelize)
}

val buildProperties = Properties()
val buildPropertiesFile = rootProject.file("build.properties")
if (buildPropertiesFile.exists()) {
    buildProperties.load(FileInputStream(buildPropertiesFile))
}

val appVersionCode = buildProperties.getProperty("versionCode", "1").toInt()
val appVersionName = buildProperties.getProperty("versionName", "1.0.0")!!
val buildTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmm"))!!

android {
    namespace = "com.arkhe.sunmi"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.arkhe.sunmi"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = appVersionCode
        versionName = "$appVersionName.build$buildTimestamp"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.google.material)

    // Lifecycle
    implementation(libs.bundles.lifecycle)

    // Compose
    implementation(libs.bundles.compose.core)

    // Navigation
    implementation(libs.androidx.compose.navigation)

    // Coroutines
    implementation(libs.bundles.coroutines)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Koin DI
    implementation(platform(libs.koin.bom))
    implementation(libs.bundles.koin)

    // Room Database
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    // Sunmi SDK
    implementation(libs.sunmi.printerlibrary)

    // Camera & Scanning
    implementation(libs.bundles.scanning)

    // Utilities
    implementation(libs.accompanist.permissions)

    // Testing
    testImplementation(libs.bundles.testing)
    androidTestImplementation(libs.bundles.compose.testing)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}