rootProject.name = "uploader-android-client"

include(":app")
project(":app").projectDir = file("subprojects/app")

include(":feature-uploader")
project(":feature-uploader").projectDir = file("subprojects/feature/uploader")

// Convenience to provide constants for library GA coordinates
includeBuild("libraries")

// Platform for dependency versions shared by 'main build' and 'build-src'
includeBuild("platform")
