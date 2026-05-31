import com.android.build.api.dsl.ApplicationExtension

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.ksp)
  alias(libs.plugins.google.maps.secrets)
}

val appVersionName = "1.0"
val appVersionCode = 1

configure<ApplicationExtension> {
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
      System.getenv("KEYSTORE_FILE")?.let { storeFile = file(it) }
      storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
      keyAlias = System.getenv("KEY_ALIAS") ?: ""
      keyPassword = System.getenv("KEY_PASSWORD") ?: ""
    }
  }

  buildTypes {
    release {
      applicationIdSuffix = ".release"
      resValue("string", "app_name", "CaaSI")

      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
      )
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      applicationIdSuffix = ".debug"
      resValue("string", "app_name", "CaaSI🐛")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  buildFeatures {
    compose = true
    buildConfig = true
    resValues = true
  }
}

secrets {
  defaultPropertiesFileName = "secrets.defaults.properties"
}

kotlin {
  jvmToolchain(11)
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
  implementation(libs.koin.compose)

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
  implementation(libs.play.services.maps)

  // Google maps
  implementation(libs.maps.compose)
  implementation(libs.maps.compose.utils)
  implementation(libs.maps.compose.widgets)

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
