plugins {
    alias(libs.plugins.runrun.android.feature.ui)
}

android {
    namespace = "com.dj.auth.presentation"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.auth.domain)
}