// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    dependencies {
        classpath(libs.androidx.navigation.safe.args.gradle.plugin)
        classpath(libs.gradle)
        classpath(libs.google.services)
        classpath(libs.firebase.crashlytics.gradle)
        classpath(libs.perf.plugin)
        classpath(libs.hilt.android.gradle.plugin)
        classpath(libs.kotlin.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.ktlint)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory.get())
}
