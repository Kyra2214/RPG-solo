plugins { alias(libs.plugins.kotlin.jvm) }
kotlin { jvmToolchain(17) }
dependencies {
    implementation(project(":engine"))
    // MI-09: o molde de Mythralis (WorldTemplate) é lido pelo `world`; a lista de mundos da nova campanha o expõe.
    api(project(":world"))
    testImplementation(libs.junit)
}
