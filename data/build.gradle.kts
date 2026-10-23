plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}
android {
    namespace = "dev.rpgsolo.data"
    compileSdk = 35
    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    // Q-04: o MigrationTestHelper lê os esquemas exportados (schemas/<banco>/<versão>.json) como assets do teste.
    sourceSets.getByName("androidTest").assets.srcDir("$projectDir/schemas")
}
// Esquemas versionados do banco (3.1.2): ficam no repositório para conferir migrações.
ksp { arg("room.schemaLocation", "$projectDir/schemas") }
dependencies {
    implementation(project(":engine"))
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    androidTestImplementation(libs.room.testing)
    androidTestImplementation(libs.junit)
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
}
