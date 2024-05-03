plugins {
    alias(libs.plugins.runrun.android.library)
    alias(libs.plugins.runrun.jvm.ktor)
}

android {
    namespace = "com.dj.run.network"
}

dependencies {

    implementation(projects.core.domain)
    implementation(projects.core.data)

}