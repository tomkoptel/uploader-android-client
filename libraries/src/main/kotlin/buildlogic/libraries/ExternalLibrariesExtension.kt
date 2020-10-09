package buildlogic.libraries

abstract class ExternalLibrariesExtension {
    val tests = Tests
    val androidTests = AndroidTests
    val utils = Utils
    val ui = UI
    val network = Network
    val di = DI

    object Tests {
        val mockk = "io.mockk:mockk"
        val kluent = "org.amshove.kluent:kluent"
        val junit4 = "junit:junit"
    }

    object AndroidTests {
        val junitRules = "androidx.test.ext:junit"
        val espressoCore = "androidx.test.espresso:espresso-core"
        val junitRunner = "androidx.test:runner"
        val testCore = "androidx.test:core-ktx"
    }

    object Utils {
        val fileSize = "com.jakewharton.byteunits:byteunits"
        val documentFile = "androidx.documentfile:documentfile"
        val rxKotlin = "io.reactivex.rxjava3:rxkotlin"
        val rxJava = "io.reactivex.rxjava3:rxjava"
        val timber = "com.jakewharton.timber:timber"
        val androidCollections = "androidx.collection:collection-ktx"
    }

    object Network {
        val retrofit = "com.squareup.retrofit2:retrofit"
        val retrofitRxAdapter = "com.squareup.retrofit2:adapter-rxjava3"
        val okhttp3Logging = "com.squareup.okhttp3:logging-interceptor"
        val mockwebserver = "com.squareup.okhttp3:mockwebserver"
        val okreplay = "com.airbnb.okreplay:okreplay"
        val okreplayJunit = "com.airbnb.okreplay:junit"
    }

    object UI {
        val coreAndroidX = "androidx.core:core-ktx"
        val appcompat = "androidx.appcompat:appcompat"
        val materialDesign = "com.google.android.material:material"
        val constraintlayout = "androidx.constraintlayout:constraintlayout"
        val navigationFragment = "androidx.navigation:navigation-fragment-ktx"
        val navigationUI = "androidx.navigation:navigation-ui-ktx"
        val lifecycleExt = "androidx.lifecycle:lifecycle-extensions"
        val lifecycleCommonJava8 = "androidx.lifecycle:lifecycle-common-java8"
        val fragmentKtx = "androidx.fragment:fragment-ktx"
        val activityKtx = "androidx.activity:activity-ktx"
    }

    object DI {
        val hiltCore = "com.google.dagger:hilt-core"
        val hiltAndroid = "com.google.dagger:hilt-android"
        val hiltCompiler = "com.google.dagger:hilt-android-compiler"
        val hiltTesting = "com.google.dagger:hilt-android-testing"
        val hiltViewModel = "androidx.hilt:hilt-lifecycle-viewmodel"
        val hiltViewModelCompiler = "androidx.hilt:hilt-compiler"
    }
}
