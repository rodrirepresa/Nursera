import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class StrictClassVisibilityConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.withId("org.jetbrains.kotlin.android") {
                extensions.configure<KotlinAndroidProjectExtension> { explicitApi() }
            }
            plugins.withId("org.jetbrains.kotlin.jvm") {
                extensions.configure<KotlinJvmProjectExtension> { explicitApi() }
            }
        }
    }
}
