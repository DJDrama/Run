plugins {
    `kotlin-dsl`
}

group = "com.dj.www.buildlogic"

dependencies {
    // will be included only during the compile-time
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication"){
            id = "runrun.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose"){
            id = "runrun.android.application.compose"
            implementationClass = "AndroidApplicationComposeConvention"
        }
    }
}