plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.ksp)
  alias(libs.plugins.google.maps.secrets)
}

android {
  namespace = "eu.slickbot.caasi"

  defaultConfig {
    applicationId = namespace

    minSdk = 26
    targetSdk = 35
    compileSdk = 35

    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions {
    jvmTarget = "11"
  }

  buildFeatures {
    compose = true
    buildConfig = true
  }
}

dependencies {
  // Android
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)

  // Koin
  implementation(libs.koin.compose)

  // Datastore
  implementation(libs.androidx.datastore.preferences)

  // Room
//  implementation(libs.androidx.room.runtime)
//  implementation(libs.androidx.room.ktx)
//  ksp(libs.androidx.room.compiler)

  // OkHttp
  implementation(libs.okhttp)
  implementation(libs.okhttp.logging)

  // Retrofit
//  implementation(libs.retrofit)
//  implementation(libs.retrofit.moshi)

  // Moshi
  implementation(libs.moshi.kotlin)
  implementation(libs.moshi.adapters)
  ksp(libs.moshi.codegen)

  // Google maps
  implementation(libs.play.services.maps)
  implementation(libs.maps.compose)
  implementation(libs.maps.compose.utils)
  implementation(libs.maps.compose.widgets)

  // Test
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
}
