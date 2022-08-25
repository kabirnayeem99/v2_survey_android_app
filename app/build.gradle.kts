plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "io.github.kabirnayeem99.v2_survey"
        minSdk = 26
        targetSdk = 32
        versionCode = 2
        versionName = "1.0.1-alpha"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }

}


dependencies {

    // core
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.5.0")

    // layout
    implementation("com.google.android.material:material:1.7.0-beta01")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.1")

    // timber - for better logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // dagger-hilt - for dependency injection
    val hiltVersion = "2.43.2"
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")

    // retrofit - for network call
    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    val gson_version = "2.9.1"
    implementation("com.google.code.gson:gson:$gson_version")// gson for json serialisation
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.9")

    // easy_permission - for handling permission
    implementation("pub.devrel:easypermissions:3.0.0")

    // room - for storing data locally
    val roomVersion = "2.4.3"
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    // testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test:core:1.4.0")
    testImplementation("org.mockito:mockito-core:4.7.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation("io.mockk:mockk:1.12.7")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("org.mockito:mockito-inline:2.13.0")
    testImplementation("org.junit.jupiter:junit-jupiter")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

}


kapt {
    correctErrorTypes = true
}