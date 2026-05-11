plugins {
    id("nursera.android.module")
    id("nursera.kotlin.android")
    id("nursera.compose")
}

android {
    namespace = "com.rodrirepresa.nursera.core.ui"
}

dependencies {

    // Libraries
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
}
