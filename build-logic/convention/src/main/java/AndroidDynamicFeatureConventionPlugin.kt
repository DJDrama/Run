import com.android.build.gradle.internal.dsl.DynamicFeatureExtension
import com.dj.convention.ExtensionType
import com.dj.convention.configureAndroidCompose
import com.dj.convention.configureBuildTypes
import com.dj.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidDynamicFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("com.android.dynamic-feature")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<DynamicFeatureExtension> {
                configureKotlinAndroid(commonExtension = this)
                configureAndroidCompose(this)

                configureBuildTypes(
                    commonExtension = this,
                    extensionType = ExtensionType.DYNAMIC_FEATURE,
                )
            }

            dependencies {
                addUiLayerDependencies(project = target)
                "testImplementation"(kotlin("test"))
            }
        }
    }

}