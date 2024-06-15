plugins {
    alias(libs.plugins.runrun.android.library)
    alias(libs.plugins.runrun.android.room)
}

android {
    namespace = "com.dj.analytics.data"
}

dependencies {
    implementation(libs.bundles.koin)

    implementation(libs.kotlinx.coroutines.core)
    implementation(projects.core.database)
    implementation(projects.core.domain)
    implementation(projects.analytics.domain)
}