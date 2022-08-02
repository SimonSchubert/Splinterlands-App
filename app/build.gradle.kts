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
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
//    lint {
//        abortOnError = false
//    }
}

dependencies {
    implementation("io.ktor:ktor-client-core:2.0.3")
    implementation("io.ktor:ktor-client-cio:2.0.3")
    implementation("io.ktor:ktor-client-content-negotiation:2.0.3")
    implementation("io.ktor:ktor-client-android:2.0.3")
    implementation("io.ktor:ktor-client-serialization:2.0.3")
    implementation("io.ktor:ktor-client-gson:2.0.3")
    implementation("io.ktor:ktor-client-json:2.0.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("jp.wasabeef:picasso-transformations:2.4.0")

    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.1")
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
