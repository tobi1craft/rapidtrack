plugins {
    application
}

java {
    sourceCompatibility = JavaVersion.VERSION_23
    targetCompatibility = JavaVersion.VERSION_23
}

if (JavaVersion.current().isJava9Compatible) {
    tasks.withType<JavaCompile> {
        options.release.set(23)
    }
}

val mainClassName = "de.tobi1craft.rapidtrack.server.ServerLauncher"

application {
    mainClass.set(mainClassName)
}

dependencies {
    implementation(project(":shared"))
}

tasks.jar {
    // appName aus dem übergeordneten Projekt abrufen
    // get the appName from the root project!


    archiveBaseName.set(project.extra["appName"] as String)

    // the duplicatesStrategy matters starting in Gradle 7.0; this setting works.
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn(configurations.runtimeClasspath)

    from({ configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) } })

    // diese "exclude" Zeilen entfernen unnötige doppelte Dateien im Output-JAR.
    exclude("META-INF/INDEX.LIST", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    dependencies {
        exclude("META-INF/INDEX.LIST", "META-INF/maven/**")
    }
    // Das Manifest macht das JAR ausführbar.
    manifest {
        attributes["Main-Class"] = mainClassName
    }
    // Dieser letzte Schritt kann auf einigen Betriebssystemen helfen, die zusätzliche Anweisungen für ausführbare JARs benötigen.
    doLast {
        file(archiveFile).setExecutable(true, false)
    }
}

// Equivalent to the jar task; hier für die Kompatibilität mit gdx-setup.
tasks.register("dist", Jar::class) {
    dependsOn(tasks.jar)
}
