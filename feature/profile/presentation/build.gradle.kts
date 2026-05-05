plugins {
    id("nursera.android.library.compose")
}

android {
    namespace = "com.rodrirepresa.nursera.feature.profile.presentation"
}

dependencies {

    // Projects
    implementation(projects.feature.profile.domain)

    // Libraries
    implementation(libs.adidas.mvi)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
}
