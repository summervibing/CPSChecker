plugins {
    id("java")
}

group = "de.marvin"
version = "1.0-SNAPSHOT"

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
    mavenLocal()

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
}

dependencies {
    compileOnly("org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")

    compileOnly("org.jetbrains:annotations:26.0.2")

    // For testing purposes
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito:mockito-core:5.17.0")
    testImplementation("org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

val testServerDir = file("test-server")
val pluginsDir = File(testServerDir, "plugins")

tasks.register<Copy>("copyJarToTestServer") {
    dependsOn("jar")
    from("${buildDir}/libs/${project.name}-$version.jar")
    into(pluginsDir)
}

tasks.named("build") {
    finalizedBy("copyJarToTestServer")
}