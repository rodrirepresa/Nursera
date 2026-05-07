plugins {
    id("nursera.android.module")
    id("nursera.kotlin.android")
    id("nursera.hilt.android")
}

android {
    namespace = "com.rodrirepresa.nursera.feature.hospital.data"
}

dependencies {

    // Projects
    implementation(projects.feature.hospital.domain)

    // Libraries
    implementation(libs.kotlinx.coroutines.core)
}
