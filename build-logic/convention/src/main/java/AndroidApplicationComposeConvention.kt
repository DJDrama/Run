import com.android.build.api.dsl.ApplicationExtension
import com.dj.convention.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationComposeConvention : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.apply("runrun.android.application")
            val extension = extensions.getByType<ApplicationExtension>()
            configureAndroidCompose(extension)
        }
    }
}