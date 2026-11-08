plugins { alias(libs.plugins.kotlin.jvm) }
kotlin { jvmToolchain(17) }
sourceSets {
    named("test") {
        // O molde exportado (MI-01) fica nos recursos de `rules`; os testes de conformidade o leem pelo classpath, como o app.
        resources.srcDir(rootProject.file("rules/src/main/resources"))
    }
}
tasks.test { maxHeapSize = "1g" }
dependencies {
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
}
