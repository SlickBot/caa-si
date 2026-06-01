plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.ksp)
}

val appVersionName = "1.0"
val appVersionCode = 1

android {
  namespace = "eu.slickbot.caasi"

  defaultConfig {
    applicationId = namespace

    minSdk = 26
    targetSdk = 37
    compileSdk = 37

    versionName = appVersionName
    versionCode = appVersionCode

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      storeFile = System.getenv("KEYSTORE_FILE")?.let { file(it) }
      storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
      keyAlias = System.getenv("KEY_ALIAS") ?: ""
      keyPassword = System.getenv("KEY_PASSWORD") ?: ""
    }
  }

  buildTypes {
    release {
      applicationIdSuffix = ".release"
      resValue("string", "app_name", "CaaSI")

      isMinifyEnabled = true
      isShrinkResources = true
      signingConfig = signingConfigs.getByName("release")
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
    debug {
      applicationIdSuffix = ".debug"
      resValue("string", "app_name", "CaaSI🐛")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }

  buildFeatures {
    compose = true
    buildConfig = true
    resValues = true
  }

  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

kotlin {
  jvmToolchain(21)
  compilerOptions {
    freeCompilerArgs.add("-Xannotation-default-target=param-property")
  }
}

tasks.withType<Test>().configureEach {
  useJUnit {
    if (project.hasProperty("liveTests")) {
      includeCategories("eu.slickbot.caasi.LiveNetwork")
    } else {
      excludeCategories("eu.slickbot.caasi.LiveNetwork")
    }
  }
}

dependencies {
  // Android & Compose
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.material.icons.extended)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)

  // Koin
  implementation(libs.koin.androidx.compose)

  // Datastore
  implementation(libs.androidx.datastore.preferences)

  // Room
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  ksp(libs.androidx.room.compiler)

  // OkHttp
  implementation(libs.okhttp)
  implementation(libs.okhttp.logging)

  // Moshi
  implementation(libs.moshi.kotlin)
  implementation(libs.moshi.adapters)
  ksp(libs.moshi.codegen)

  // Google play services
  implementation(libs.play.services.location)

  // MapLibre
  implementation(libs.maplibre.android)        // kept: org.maplibre.android.geometry.LatLng/LatLngBounds (domain types)
  implementation(libs.maplibre.compose)
  implementation(libs.maplibre.compose.material3)

  // Test
  testImplementation(libs.junit)
  testImplementation(libs.okhttp.mockwebserver)
  testImplementation(libs.okhttp.tls)
  testImplementation(libs.mockk)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
}
