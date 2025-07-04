plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.21"
}

android {
    compileSdk = 35

    defaultConfig {
        applicationId = "com.splintergod.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 15
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    lint {
        abortOnError = false
    }
    namespace = "com.splintergod.app"
}

dependencies {
    implementation("io.ktor:ktor-client-core:3.1.3")
    implementation("io.ktor:ktor-client-android:3.1.3")
    implementation("io.ktor:ktor-client-logging:3.1.3")
    implementation("com.google.code.gson:gson:2.13.1")


    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.material:material:1.8.2")
    implementation("androidx.compose.material:material-icons-core:1.7.8")
    implementation("androidx.compose.animation:animation:1.8.2")
    implementation("androidx.compose.ui:ui-tooling:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.9.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")
    implementation("io.coil-kt:coil:2.7.0")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("io.coil-kt:coil-gif:2.7.0")


    implementation("io.insert-koin:koin-core:4.0.4")
    implementation("io.insert-koin:koin-android:4.0.4")
    implementation("io.insert-koin:koin-androidx-compose:4.0.4") // Or the version compatible with Koin 4.0.4

    androidTestImplementation("io.ktor:ktor-client-mock:3.1.3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.8.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.8.2")
    debugImplementation("androidx.compose.ui:ui-tooling:1.8.2")
}
