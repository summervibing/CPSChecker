dependencies {
    // Spigot API
    compileOnly(libs.spigot.latest)

    // For testing purposes
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform)
    testImplementation(libs.mockito)
    testImplementation(libs.spigot.latest)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
