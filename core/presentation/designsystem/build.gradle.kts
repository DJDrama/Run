plugins {
    alias(libs.plugins.runrun.android.library.compose)
}

android {
    namespace = "com.dj.core.presentation.designsystem"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // by using api, all modules depend on designsystem module can access material3 related stuff
    api(libs.androidx.compose.material3)

}