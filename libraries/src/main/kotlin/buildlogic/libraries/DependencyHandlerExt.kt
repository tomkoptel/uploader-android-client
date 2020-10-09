@file:JvmName("DependencyHandlerExt")

package buildlogic.libraries

import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.kotlin

private val libs = object : ExternalLibrariesExtension() {}

private fun DependencyHandler.testImplementation(dependencyNotation: Any) {
    add("testImplementation", dependencyNotation)
}

private fun DependencyHandler.androidTestImplementation(dependencyNotation: Any) {
    add("androidTestImplementation", dependencyNotation)
}

private fun DependencyHandler.implementation(dependencyNotation: Any) {
    add("implementation", dependencyNotation)
}

fun DependencyHandler.unitTests() {
    testImplementation(libs.tests.mockk)
    testImplementation(libs.tests.kluent)
    testImplementation(libs.tests.junit4)
    // To get JUnit errors from kotlin.test, to e.g. enable diff windows in failure messages
    testImplementation(kotlin("test-junit"))
}

fun DependencyHandler.instrumentationTests() {
    androidTestImplementation(libs.androidTests.testCore)
    androidTestImplementation(libs.androidTests.junitRunner)
    androidTestImplementation(libs.androidTests.junitRules)
    androidTestImplementation(libs.androidTests.espressoCore)
}

fun DependencyHandler.fileManagement() {
    // FileSize convenience API (aka TimeUnit equivalent)
    implementation(libs.utils.fileSize)

    // Simplifies requesting file from the system relying of OS file picker
    implementation(libs.utils.documentFile)
}

fun DependencyHandler.coreUI() {
    implementation(libs.ui.coreAndroidX)
    implementation(libs.ui.appcompat)
    implementation(libs.ui.materialDesign)
    implementation(libs.ui.constraintlayout)

    implementation(libs.ui.fragmentKtx)
    implementation(libs.ui.activityKtx)

    implementation(libs.ui.navigationFragment)
    implementation(libs.ui.navigationUI)

    // For LifecycleService dep
    implementation(libs.ui.lifecycleExt)
    implementation(libs.ui.lifecycleCommonJava8)
}

fun DependencyHandler.rx() {
    implementation(libs.utils.rxJava)
    implementation(libs.utils.rxKotlin)
}

fun DependencyHandler.networking() {
    implementation(libs.network.retrofit)
    implementation(libs.network.retrofitRxAdapter)
    implementation(libs.network.okhttp3Logging)

    testImplementation(libs.network.mockwebserver)
    testImplementation(libs.network.okreplay)
    testImplementation(libs.network.okreplayJunit)
}

fun DependencyHandler.logging() {
    implementation(libs.utils.timber)
}

// FIXME investigate why kapt does not recognizes platform
fun DependencyHandler.hiltAndroid() {
    val hiltVersion = ":2.29.1-alpha"
    val hiltViewModelVersion = ":1.0.0-alpha02"

    implementation(libs.di.hiltCore + hiltVersion)
    implementation(libs.di.hiltAndroid + hiltVersion)
    add("kapt", libs.di.hiltCompiler + hiltVersion)

    add("kaptAndroidTest", libs.di.hiltCompiler + hiltVersion)
    androidTestImplementation(libs.di.hiltTesting + hiltVersion)

    implementation(libs.di.hiltViewModel + hiltViewModelVersion)
    add("kapt", libs.di.hiltViewModelCompiler + hiltViewModelVersion)
}
