import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("org.jlleitschuh.gradle.ktlint")
    id("com.google.devtools.ksp")
}

android {
    val target = 35
    buildToolsVersion = "35.0.0"
    namespace = "com.token.tokenator"
    compileSdk = target

    defaultConfig {
        applicationId = "com.token.tokenator"
        minSdk = 24
        targetSdk = target
        versionCode = 60
        versionName = "1.79"

        testInstrumentationRunner = "com.token.tokenator.HiltAndroidJUnitRunner"
        testInstrumentationRunnerArguments.putAll(mutableMapOf("clearPackageData" to "true"))
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            buildConfigField("String", "SECRET_KEY", "\"${System.getenv("SECRET_KEY") ?: project.property("SECRET_KEY")} \"")
            buildConfigField("String", "SALT", "\"${System.getenv("SALT") ?: project.property("SALT")}\"")
            buildConfigField("String", "IV", "\"${System.getenv("IV") ?: project.property("IV")}\"")
        }

        debug {
            buildConfigField("String", "SECRET_KEY", "\"${System.getenv("SECRET_KEY") ?: project.property("SECRET_KEY")} \"")
            buildConfigField("String", "SALT", "\"${System.getenv("SALT") ?: project.property("SALT")}\"")
            buildConfigField("String", "IV", "\"${System.getenv("IV") ?: project.property("IV")}\"")

            isMinifyEnabled = false

            applicationIdSuffix = ".debug"
            versionNameSuffix = " debug"
            resValue(type = "string", name = "app_name", value = "Tokenator debug")
        }
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        animationsDisabled = true
    }

    kapt {
        correctErrorTypes = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += "META-INF/*"
        }
    }
}

ktlint {
    android = true
    ignoreFailures = false
    reporters {
        reporter(ReporterType.CHECKSTYLE)
        reporter(ReporterType.JSON)
        reporter(ReporterType.HTML)
    }
    additionalEditorconfig.set(
        mapOf(
            "max_line_length" to "off",
            "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
        ),
    )
}

tasks.named("preBuild") {
    dependsOn("ktlintFormat")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)

    // Lifecycle
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Room Database
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)

    implementation(libs.androidx.room.ktx)

    implementation(libs.app.update)
    implementation(libs.app.update.ktx)

    // To use Kotlin Symbol Processing (KSP)
    ksp(libs.androidx.room.compiler)

    // optional - Test helpers
    testImplementation(libs.androidx.room.testing)

    // optional - Paging 3 Integration
    implementation(libs.androidx.room.paging)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.dynamic.features.fragment)

    // DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)

    // Firebase
    // Import the BoM for the Firebase platform
    implementation(platform(libs.firebase.bom))

    // Add the dependencies for the Crashlytics and Analytics libraries
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.firebase.crashlytics.ktx)

    // Add the dependency for the Realtime Database library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.firebase.database.ktx)

    // Add the dependency for the Firebase SDK for Google Analytics
    implementation(libs.firebase.analytics.ktx)

    // This dependency is downloaded from the Google’s Maven repository.
    // Make sure you also include that repository in your project's build.gradle file.
    implementation(libs.feature.delivery)

    // For Kotlin users, also import the Kotlin extensions library for Play Feature Delivery:
    implementation(libs.feature.delivery.ktx)

    // Add Performance lib
    implementation(libs.firebase.perf.ktx)

    // For instrumented tests.
    androidTestImplementation(libs.hilt.android.testing)
    // ...with Kotlin.
    kaptAndroidTest(libs.hilt.android.compiler)

    // Styling
    implementation(libs.material)

    // DataBinding & other
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Feature discovery
    implementation(libs.material.tap.target.prompt)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // TESTING
    // Testing Navigation
    androidTestImplementation(libs.androidx.navigation.testing)

    // Required -- JUnit 4 framework
    testImplementation(libs.junit)

    // Optional -- Mockito framework
    testImplementation(libs.mockito.core)

    // Core library
    androidTestImplementation(libs.androidx.core)

    // AndroidJUnitRunner and JUnit Rules
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(libs.accessibility.test.framework)
    androidTestImplementation(libs.espresso.library.kotlin)

    androidTestUtil(libs.androidx.orchestrator)
}
