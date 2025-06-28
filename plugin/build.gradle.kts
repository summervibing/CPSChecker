dependencies {
    // Spigot API
    compileOnly(libs.spigot.latest)

    // Core dependency
    compileOnly(project(":core"))

    // Protocol dependencies
    compileOnly(project(":protocol-v1_8_8"))
}

val pluginName = project.property("pluginName").toString()
val version = project.property("version").toString()
val author = project.property("author").toString()
val pluginDescription = project.property("description").toString()

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(
            "pluginName" to pluginName,
            "version" to version,
            "author" to author,
            "description" to pluginDescription
        )
    }
}