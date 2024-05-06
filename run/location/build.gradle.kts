plugins {
    alias(libs.plugins.runrun.android.library)
}

android {
    namespace = "com.dj.run.location"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.koin)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.google.android.gms.play.services.location)

    implementation(projects.core.domain)
    implementation(projects.run.domain)
}