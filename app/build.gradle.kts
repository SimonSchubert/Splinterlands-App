plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.splinterlandstest"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "0.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            // isMinifyEnabled = true
            // proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }
    lint {
        abortOnError = false
    }
    namespace = "com.example.splinterlandstest"
}

dependencies {
    implementation("io.ktor:ktor-client-core:2.1.3")
    implementation("io.ktor:ktor-client-cio:2.1.3")
    implementation("io.ktor:ktor-client-content-negotiation:2.1.3")
    implementation("io.ktor:ktor-client-android:2.1.3")
    implementation("io.ktor:ktor-client-serialization:2.1.3")
    implementation("io.ktor:ktor-client-gson:2.1.3")
    implementation("io.ktor:ktor-client-json:2.1.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.1.3")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("androidx.compose.material:material:1.3.1")
    implementation("androidx.compose.animation:animation:1.3.1")
    implementation("androidx.compose.ui:ui-tooling:1.3.1")
    implementation("androidx.navigation:navigation-compose:2.5.3")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.27.0")
    implementation("com.google.accompanist:accompanist-flowlayout:0.27.0")
    implementation("io.coil-kt:coil-compose:2.2.2")

    implementation("io.insert-koin:koin-core:3.2.2")
    implementation("io.insert-koin:koin-android:3.3.0")

    androidTestImplementation("io.ktor:ktor-client-mock:2.1.3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.4")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.3.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.3.1")
    debugImplementation("androidx.compose.ui:ui-tooling:1.3.1")
}
