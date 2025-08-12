import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties
import java.util.TimeZone

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
val appVersionName: String? = buildProperties.getProperty("versionName", "1.0.0")
val appProductName: String? = buildProperties.getProperty("productName", "arkhe")
val now: Instant? = Clock.systemUTC().instant()
val localDateTime: LocalDateTime? = now?.atZone(TimeZone.getDefault().toZoneId())?.toLocalDateTime()
val timestampFormatter: DateTimeFormatter? = DateTimeFormatter.ofPattern("yyMMddHHmm")
val timestampString = localDateTime?.format(timestampFormatter)
val buildTimestamp = timestampString

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

base {
    archivesName.set("$appProductName-$appVersionName.build$buildTimestamp")
}

dependencies {
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.window)
    implementation(libs.google.material)

    // Lifecycle
    implementation(libs.bundles.lifecycle)

    // Compose
    implementation(libs.bundles.compose.core)
    implementation(libs.androidx.constraintlayout.compose)

    // Navigation
    implementation(libs.androidx.compose.navigation)

    // Coroutines
    implementation(libs.bundles.coroutines)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Ktor Client
    implementation(libs.bundles.ktor.client)

    // Koin DI
    implementation(platform(libs.koin.bom))
    implementation(libs.bundles.koin)

    // Room Database
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    // DataStore
    implementation(libs.bundles.datastore)

    // Sunmi SDK
    implementation(libs.sunmi.printerlibrary)

    // Camera & Scanning
    implementation(libs.bundles.camera)
    implementation(libs.bundles.scanning)

    // Utilities
    implementation(libs.accompanist.permissions)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.coroutines.android)

    // Testing
    testImplementation(libs.bundles.testing)
    androidTestImplementation(libs.bundles.compose.testing)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}