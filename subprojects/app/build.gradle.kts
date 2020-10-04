import buildlogic.libraries.coreUI
import buildlogic.libraries.fileManagement
import buildlogic.libraries.hiltAndroid
import buildlogic.libraries.instrumentationTests
import buildlogic.libraries.logging
import buildlogic.libraries.rx
import buildlogic.libraries.unitTests
import com.android.build.gradle.internal.dsl.DefaultConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("buildlogic.libraries")
    id("com.android.application")
    id("dagger.hilt.android.plugin")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("androidx.navigation.safeargs")
}

val appId = "com.olderworld.app.uploader"

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.2")

    defaultConfig {
        applicationId = appId
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "$appId.TestAppTestRunner"

        addStringConstant(
            name = "ACTION_UPLOAD",
            value = "$appId.ACTION_UPLOAD"
        )
        stringConfigField(
            name = "TRACKING_CHANNEL_ID",
            value = "$appId.TRACKING_CHANNEL_ID"
        )
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        val main by getting
        main.java.srcDirs("src/main/kotlin", "src/main/kotlinX")

        val test by getting
        test.java.srcDirs("src/test/kotlin", "src/main/kotlinX")
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
        unitTests.isReturnDefaultValues = true
    }
}

tasks.withType(KotlinCompile::class.java).all {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation(platform("com.olderwold.uploader:platform"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":feature-uploader"))

    rx()
    hiltAndroid()
    coreUI()
    fileManagement()
    logging()

    unitTests()
    instrumentationTests()
}

fun DefaultConfig.addStringConstant(name: String, value: String) {
    manifestPlaceholders[name] = value
    stringConfigField(name, value)
}

fun DefaultConfig.stringConfigField(name: String, value: String) {
    buildConfigField(String::class.java.simpleName, name, "\"$value\"")
}
