plugins {
    alias(libs.plugins.runrun.android.library)
}

android {
    namespace = "com.dj.run.network"
}

dependencies {

    implementation(projects.core.domain)
    implementation(projects.core.data)

}