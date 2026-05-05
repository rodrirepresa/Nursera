plugins {
    id("nursera.android.library.compose")
    id("nursera.android.library.hilt")
}

android {
    namespace = "com.rodrirepresa.nursera.feature.home.presentation"
}

dependencies {

    // Projects
    implementation(projects.feature.home.domain)
    implementation(projects.core.common)

    // Libraries
    implementation(libs.adidas.mvi)
    implementation(libs.adidas.mvi.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Test
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.androidx.arch.core.testing)
}
