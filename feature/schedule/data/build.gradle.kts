plugins {
    id("nursera.android.module")
    id("nursera.kotlin.android")
    id("nursera.hilt.android")
}

android {
    namespace = "com.rodrirepresa.nursera.feature.schedule.data"
}

dependencies {

    // Projects
    implementation(projects.feature.schedule.domain)

    // Libraries
    implementation(libs.kotlinx.coroutines.core)
}
