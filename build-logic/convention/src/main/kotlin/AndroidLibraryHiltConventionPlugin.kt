import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibraryHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("nursera.android.library")
                apply("com.google.dagger.hilt.android")
                apply("com.google.devtools.ksp")
            }
        }
    }
}
