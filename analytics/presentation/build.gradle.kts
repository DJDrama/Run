plugins {
    alias(libs.plugins.runrun.android.feature.ui)
}

android {
    namespace = "com.dj.analytics.presentation"
}

dependencies {
    implementation(projects.analytics.domain)
}