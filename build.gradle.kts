plugins {
    `java-library`
    alias(libs.plugins.shadow)
}

subprojects {
    val libs = rootProject.libs

    apply {
        plugin("java-library")
    }

    group = project.property("group") as String
    version = project.property("version") as String
    description = project.property("description") as String

    dependencies {
        // Google Guice
        implementation(libs.guice)
        // Annotations
        compileOnly(libs.jetbrains.annotations)
        // ProtocolLib
        compileOnly(libs.protocollib)
    }

    tasks.compileJava {
        options.encoding = Charsets.UTF_8.name()
    }
}

dependencies {
    subprojects.forEach() {
        api(project(it.path))
    }
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        dependsOn(subprojects.map {
            it.tasks.named("assemble")
        })

        relocate("com.google.common", "de.marvin.libs.guava")

        archiveFileName.set("${project.property("name")}-${project.property("version")}.jar")
        minimize()
    }
}