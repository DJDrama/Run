plugins {
    alias(libs.plugins.runrun.jvm.library)
    alias(libs.plugins.runrun.jvm.junit5)
}

dependencies{
    implementation(libs.kotlinx.coroutines.core)
    implementation(projects.core.domain)

    testImplementation(projects.core.test)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)


}