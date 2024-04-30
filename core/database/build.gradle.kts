plugins {
    alias(libs.plugins.runrun.android.library)
    alias(libs.plugins.runrun.android.room)
}

android {
    namespace = "com.dj.core.database"
}

dependencies {
    // Timber
    implementation(libs.org.mongodb.bson)
    implementation(projects.core.domain)

}