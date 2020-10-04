import buildlogic.libraries.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    kotlin("android")
    id("buildlogic.libraries")
}

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.2")

    defaultConfig {
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
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

    implementation(libs.utils.androidCollections)

    logging()
    rx()
    networking()

    unitTests()
}
