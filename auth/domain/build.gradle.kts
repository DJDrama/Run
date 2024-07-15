plugins {
    alias(libs.plugins.runrun.jvm.library)
    alias(libs.plugins.runrun.jvm.junit5)
}

dependencies {
    implementation(projects.core.domain)
}