plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.plugins.android.application.toDep())
    compileOnly(libs.plugins.android.library.toDep())
    compileOnly(libs.plugins.kotlin.android.toDep())
    compileOnly(libs.plugins.kotlin.jvm.toDep())
    compileOnly(libs.plugins.kotlin.compose.toDep())
    compileOnly(libs.plugins.kotlin.parcelize.toDep())
    compileOnly(libs.plugins.hilt.toDep())
    compileOnly(libs.plugins.ksp.toDep())
    compileOnly(libs.plugins.ktlint.toDep())
}

fun Provider<PluginDependency>.toDep() = map {
    "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}"
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "nursera.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "nursera.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "nursera.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidLibraryHilt") {
            id = "nursera.android.library.hilt"
            implementationClass = "AndroidLibraryHiltConventionPlugin"
        }
        register("kotlinLibrary") {
            id = "nursera.kotlin.library"
            implementationClass = "KotlinLibraryConventionPlugin"
        }
        register("ktlint") {
            id = "nursera.ktlint"
            implementationClass = "KtLintConventionPlugin"
        }
        register("androidModule") {
            id = "nursera.android.module"
            implementationClass = "AndroidModuleConventionPlugin"
        }
        register("kotlinAndroid") {
            id = "nursera.kotlin.android"
            implementationClass = "KotlinAndroidConventionPlugin"
        }
        register("compose") {
            id = "nursera.compose"
            implementationClass = "ComposeConventionPlugin"
        }
        register("hiltAndroid") {
            id = "nursera.hilt.android"
            implementationClass = "HiltAndroidConventionPlugin"
        }
        register("strictClassVisibility") {
            id = "nursera.strict.class.visibility"
            implementationClass = "StrictClassVisibilityConventionPlugin"
        }
        register("parcelize") {
            id = "nursera.parcelize"
            implementationClass = "ParcelizeConventionPlugin"
        }
        register("junit") {
            id = "nursera.junit"
            implementationClass = "JunitConventionPlugin"
        }
    }
}
