plugins {
    alias(libs.plugins.runrun.android.library)
    alias(libs.plugins.runrun.jvm.ktor)
}

android {
    namespace = "com.dj.core.data"
}

dependencies {
    // Timber
    implementation(libs.timber)

    implementation(projects.core.domain)
    implementation(projects.core.database)

}