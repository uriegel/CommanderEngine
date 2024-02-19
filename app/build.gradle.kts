plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "de.uriegel.commanderengine"
    compileSdk = 34

    signingConfigs {
        create("signing") {
            storeFile = file("/home/uwe/Dokumente/Entwicklung/AndroidKeyStore/keystore.jks")
            storePassword = extra["ANDROID_STORE_PASSWORD"].toString()
            keyAlias = "androidKey"
            keyPassword = extra["ANDROID_KEY_PASSWORD"].toString()
        }
    }

    defaultConfig {
        applicationId = "de.uriegel.commanderengine"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("signing")
        }
        debug {
            signingConfig = signingConfigs.getByName("signing")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/*"
        }
    }
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }
}
dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui:1.6.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("com.google.accompanist:accompanist-permissions:0.28.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.1")
}