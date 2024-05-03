plugins {
    alias(libs.plugins.runrun.android.library)
    alias(libs.plugins.runrun.jvm.ktor)
}

android {
    namespace = "com.dj.auth.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.auth.domain)
}