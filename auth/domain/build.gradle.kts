plugins {
    alias(libs.plugins.runrun.jvm.library)
}

dependencies {
    implementation(projects.core.domain)
}