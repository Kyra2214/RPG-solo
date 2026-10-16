plugins { alias(libs.plugins.kotlin.jvm) }
kotlin { jvmToolchain(17) }
dependencies {
    implementation(project(":engine"))
    implementation(project(":rules"))
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
}
