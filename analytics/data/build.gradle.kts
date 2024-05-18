plugins {
    alias(libs.plugins.runrun.android.library)
}

android {
    namespace = "com.dj.analytics.data"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(projects.core.database)
    implementation(projects.core.domain)
    implementation(projects.analytics.domain)
}