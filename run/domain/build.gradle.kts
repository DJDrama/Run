plugins {
    alias(libs.plugins.runrun.jvm.library)
}

dependencies{
    implementation(libs.kotlinx.coroutines.core)
    implementation(projects.core.domain)
}