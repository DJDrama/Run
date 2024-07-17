plugins {
    alias(libs.plugins.runrun.android.library)
    alias(libs.plugins.runrun.android.junit5)
}

android {
    namespace = "com.dj.android_test"
}

dependencies {
    implementation(projects.auth.data)
    implementation(projects.core.domain)
    api(projects.core.test) // use part of the API exposed by the project.

    implementation(libs.ktor.client.mock)
    implementation(libs.bundles.ktor)
    implementation(libs.coroutines.test)
}