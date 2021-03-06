plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")

    id("dagger.hilt.android.plugin")
    id("com.squareup.sqldelight")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "io.github.rsookram.rss"

        minSdk = 28
        targetSdk = 31

        versionCode = 1
        versionName = "1.0"

        resourceConfigurations += setOf("en", "anydpi")
    }

    lint {
        isCheckReleaseBuilds = false

        textReport = true

        isWarningsAsErrors = true
        isAbortOnError = true
    }

    packagingOptions.resources {
        excludes +=
            setOf(
                "kotlin/**",
                "**/DebugProbesKt.bin",
                "META-INF/*.version",
                // Public suffixes aren't needed since cookies aren't read
                // https://stackoverflow.com/questions/46168012/understand-okhttp-publicsuffixes-gz
                "okhttp3/internal/publicsuffix/*",
            )
    }

    dependenciesInfo { includeInApk = false }

    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }

        if (file("app.keystore").exists()) {
            create("release") {
                storeFile = file("app.keystore")
                storePassword = project.property("STORE_PASSWORD").toString()
                keyAlias = project.property("KEY_ALIAS").toString()
                keyPassword = project.property("KEY_PASSWORD").toString()
            }
        }
    }

    buildTypes {
        debug { signingConfig = signingConfigs.getByName("debug") }

        release {
            signingConfig =
                signingConfigs.findByName("release") ?: signingConfigs.getByName("debug")

            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("proguard-rules.pro")
        }
    }

    buildFeatures { compose = true }

    composeOptions { kotlinCompilerExtensionVersion = libs.versions.compose.get() }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.hiltNavigationCompose)
    implementation(libs.androidx.hiltWork)
    implementation(libs.androidx.viewmodelCompose)
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.paging)
    implementation(libs.androidx.workmanager)
    kapt(libs.androidx.hiltCompiler)

    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.uiTooling)

    implementation(libs.accompanist.insets)
    implementation(libs.accompanist.insetsUi)
    implementation(libs.accompanist.systemuicontroller)

    implementation(libs.retrofit)

    implementation(libs.sqldelight.android)
    implementation(libs.sqldelight.coroutines)
    implementation(libs.sqldelight.paging)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}
