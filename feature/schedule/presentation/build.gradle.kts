plugins {
    id("nursera.android.module")
    id("nursera.kotlin.android")
    id("nursera.compose")
    id("nursera.hilt.android")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rodrirepresa.nursera.feature.schedule.presentation"
}

dependencies {

    // Projects
    implementation(projects.feature.schedule.domain)
    implementation(projects.core.common)
    implementation(projects.core.ui)

    // Libraries
    implementation(libs.adidas.mvi)
    implementation(libs.adidas.mvi.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.kotlinx.immutable)

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
}
