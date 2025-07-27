plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("plugin.serialization") version "1.9.0"
    id ("com.google.gms.google-services")

}

android {
    namespace = "com.example.projettdm"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.projettdm"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true // add this
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    // Packaging configuration to resolve resource conflicts
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}" // Exclude licenses
            excludes += "META-INF/native-image/native-image.properties"
            pickFirsts += "META-INF/native-image/reflect-config.json"
            excludes += "META-INF/DEPENDENCIES"
            pickFirsts += "META-INF/native-image/native-image.properties" // Resolve conflict for this file
        }
    }
}
configurations.all {
    exclude (group= "javax.naming", module = "javax.naming-api")
}


dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.credentials)
    implementation(libs.googleid)
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0")
    implementation("io.coil-kt:coil-compose:2.2.2")
    implementation (libs.sendgrid.java)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.google.firebase.auth.ktx)
    implementation("com.google.android.gms:play-services-auth:20.6.0")
    coreLibraryDesugaring(libs.desugar.jdk.libs.v204)
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:4.10.1") {
        exclude( group ="javax.naming" , module = "javax.naming-api")
    }
    implementation ("org.slf4j:slf4j-api:2.0.0") // Or check for the latest version
    implementation ("org.slf4j:slf4j-simple:2.0.0")  // Or another SLF4J-compatible logger (e.g., Logback)
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.7.3"){
        exclude( group ="javax.naming" , module = "javax.naming-api")
    }
    implementation ("io.projectreactor:reactor-core:3.5.1")// Make sure this is the latest version
    implementation("com.google.firebase:firebase-analytics-ktx:21.3.0") // Exemple d'une bibliothèque Firebase.
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.10")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.animation.core.lint)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.protolite.well.known.types)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.androidx.material) // Replace with the latest version
    implementation(libs.ui)  // Ensure this is included
    implementation(libs.bson.kotlin)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
