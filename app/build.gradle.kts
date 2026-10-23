plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val releaseTag =
    System.getenv("GITHUB_REF_NAME")
        ?.takeIf { System.getenv("GITHUB_REF_TYPE") == "tag" && it.matches(Regex("v?[0-9]+\\.[0-9]+\\.[0-9]+([-.][0-9A-Za-z.-]+)?")) }
val configuredVersionName = System.getenv("VERSION_NAME")?.takeIf { it.isNotBlank() }
val releaseKeystorePath = System.getenv("RELEASE_KEYSTORE_PATH")?.takeIf { it.isNotBlank() }
val releaseKeystorePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")?.takeIf { it.isNotBlank() }
val releaseKeyAlias = System.getenv("RELEASE_KEY_ALIAS")?.takeIf { it.isNotBlank() }
val releaseKeyPassword = System.getenv("RELEASE_KEY_PASSWORD")?.takeIf { it.isNotBlank() }
val hasReleaseSigning = listOf(releaseKeystorePath, releaseKeystorePassword, releaseKeyAlias, releaseKeyPassword).all { it != null }
val configuredVersionCode = System.getenv("VERSION_CODE")?.toIntOrNull()?.takeIf { it > 0 }

android {
    namespace = "dev.rpgsolo"
    compileSdk = 35
    defaultConfig {
        applicationId = "dev.rpgsolo"
        minSdk = 26
        targetSdk = 35
        versionCode = configuredVersionCode ?: 1
        versionName = releaseTag?.removePrefix("v") ?: configuredVersionName ?: "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Cada teste começa com os dados do app limpos (o estado persistido de um teste não vaza para o seguinte).
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
    testOptions {
        animationsDisabled = true
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }
    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(releaseKeystorePath!!)
                storePassword = releaseKeystorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }
    buildTypes {
        getByName("release") {
            if (hasReleaseSigning) signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}
dependencies {
    implementation(project(":engine"))
    implementation(project(":data"))
    implementation(project(":ai"))
    implementation(project(":rules"))
    implementation(libs.room.runtime)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    testImplementation(libs.junit)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestUtil("androidx.test:orchestrator:1.5.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
