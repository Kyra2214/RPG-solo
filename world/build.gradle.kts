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

// WD-01 (D-09): a validação de autoria do mundo pré-configurado roda em `check`; erro de autoria quebra o build.
val validarAutoria by tasks.registering(JavaExec::class) {
    group = "verification"
    description = "Valida a autoria do molde de Mythralis (ValidadorAutoria); erro de carga ou de autoria falha a tarefa."
    classpath = sourceSets["main"].runtimeClasspath + files(rootProject.file("rules/src/main/resources"))
    mainClass.set("dev.rpgsolo.world.ValidacaoAutoria")
    args("--relatorio=${layout.buildDirectory.get().asFile}/reports/autoria/validacao-autoria.txt")
    inputs.dir(rootProject.file("rules/src/main/resources/mundos/mythralis"))
    outputs.file("${layout.buildDirectory.get().asFile}/reports/autoria/validacao-autoria.txt")
}
tasks.named("check") { dependsOn(validarAutoria) }
