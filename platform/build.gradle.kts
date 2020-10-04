plugins {
    `java-platform`
    id("buildlogic.libraries")
}

// Allow dependencies for dependencies to other platforms (BOMs)
javaPlatform.allowDependencies()

group = "com.olderwold.uploader"

dependencies {
    constraints {
        api(libs.tests.mockk) {
            version { prefer("1.10.2") }
        }
        api(libs.tests.kluent) {
            version { prefer("1.61") }
        }
        api(libs.tests.junit4) {
            version { prefer("4.13") }
        }

        api(libs.androidTests.junitRules) {
            version { prefer("1.1.2") }
        }
        val androidXtestVersion = "1.3.0"
        api(libs.androidTests.testCore) {
            version { prefer(androidXtestVersion) }
        }
        api(libs.androidTests.junitRunner) {
            version { prefer(androidXtestVersion) }
        }
        api(libs.androidTests.espressoCore) {
            version { prefer("3.3.0") }
        }

        api(libs.utils.fileSize) {
            version { prefer("0.9.1") }
        }
        api(libs.utils.documentFile) {
            version { prefer("1.0.1") }
        }
        api(libs.utils.rxKotlin) {
            version { prefer("3.0.1") }
        }
        api(libs.utils.rxJava) {
            version { prefer("3.0.6") }
        }
        api(libs.utils.timber) {
            version { prefer("4.7.1") }
        }
        api(libs.utils.androidCollections) {
            version { prefer("1.1.0") }
        }

        api(libs.ui.coreAndroidX) {
            version { prefer("1.3.1") }
        }
        api(libs.ui.appcompat) {
            version { prefer("1.2.0") }
        }
        api(libs.ui.materialDesign) {
            version { prefer("1.2.1") }
        }
        api(libs.ui.constraintlayout) {
            version { prefer("2.0.1") }
        }
        api(libs.ui.lifecycleRuntime) {
            version { prefer("2.3.0-alpha07") }
        }
        api(libs.ui.lifecycleExt) {
            version { prefer("2.2.0") }
        }

        val navigationVersion = "2.3.0"
        api(libs.ui.navigationFragment) {
            version { prefer(navigationVersion) }
        }
        api(libs.ui.navigationUI) {
            version { prefer(navigationVersion) }
        }

        val retrofitVersion = "2.9.0"
        api(libs.network.retrofit) {
            version { prefer(retrofitVersion) }
        }
        api(libs.network.retrofitRxAdapter) {
            version { prefer(retrofitVersion) }
        }

        val okHttpVersion = "4.9.0"
        api(libs.network.okhttp3Logging) {
            version { prefer(okHttpVersion) }
        }
        api(libs.network.mockwebserver) {
            version { prefer(okHttpVersion) }
        }

        val okReplayVersion = "1.6.0"
        api(libs.network.okreplay) {
            version { prefer(okReplayVersion) }
        }
        api(libs.network.okreplayJunit) {
            version { prefer(okReplayVersion) }
        }
    }
}
