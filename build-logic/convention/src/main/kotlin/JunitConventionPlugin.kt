import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

class JunitConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.withId("com.android.library") {
                extensions.configure<LibraryExtension> {
                    testOptions { unitTests.all { it.useJUnitPlatform() } }
                }
            }
            plugins.withId("com.android.application") {
                extensions.configure<BaseAppModuleExtension> {
                    testOptions { unitTests.all { it.useJUnitPlatform() } }
                }
            }
            tasks.withType<Test>().configureEach { useJUnitPlatform() }
        }
    }
}
