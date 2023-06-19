import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import tasks.GenerateKeysTask
import java.io.File

class MainPlugin: Plugin<Project> {

    companion object {
        const val GROUP = "KeyNinjaGroup"
    }

    override fun apply(p0: Project) {
        val keysFile = File("${p0.rootDir.path}/keys.json")
        if (keysFile.exists()) {

            val android = p0.extensions.findByType(BaseExtension::class.java)
                ?: throw GradleException("Project ${p0.name} is not an Android project")

            if (android is AppExtension) {
                android.applicationVariants.all { appVariant ->
                    appVariant.createGenerateKeysTask(p0)
                }
            } else {
                throw GradleException("Unsupported BaseExtension type!")
            }
        } else {
            throw GradleException("Keys.json not found")
        }
    }

    private fun ApplicationVariant.createGenerateKeysTask(project: Project) {
        val taskName = "generateKeys${name.replaceFirstChar(Char::titlecase)}"
        val provider = project.tasks.register(taskName, GenerateKeysTask::class.java) { task ->
            task.appVariant = this
            task.group = GROUP
        }
        assembleProvider.dependsOn(provider)
    }
}
