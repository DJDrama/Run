plugins {
    alias(libs.plugins.runrun.android.library)
}

android {
    namespace = "com.dj.core.database"
}

dependencies {
    // Timber
    implementation(libs.org.mongodb.bson)
    implementation(projects.core.domain)

}