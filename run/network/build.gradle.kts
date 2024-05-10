plugins {
    alias(libs.plugins.runrun.android.library)
    alias(libs.plugins.runrun.jvm.ktor)
}

android {
    namespace = "com.dj.run.network"
}

dependencies {
    implementation(libs.bundles.koin)
    implementation(projects.core.domain)
    implementation(projects.core.data)

}