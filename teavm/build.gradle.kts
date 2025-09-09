import org.teavm.gradle.api.SourceFilePolicy

plugins {
    java
    id("org.gretty") version "4.1.7"
    id("org.teavm") version "0.12.3"
}


gretty {
    contextPath = "/"
    extraResourceBase("build/dist/webapp")
}

//sourceSets["main"].resources.srcDirs.plusAssign(rootProject.file("assets"))

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${property("gdxVersion")}") //??? https://github.com/xpenatan/gdx-teavm/blob/master/examples/core/teavm/build.gradle
    implementation("com.github.xpenatan.gdx-teavm:backend-teavm:${property("gdxTeaVMVersion")}")
    implementation("com.github.xpenatan.gdx-teavm:gdx-freetype-teavm:${property("gdxTeaVMVersion")}")
    implementation("com.github.xpenatan.gdx-teavm:gdx-bullet-teavm:${property("gdxBulletTeaVMVersion")}")
    implementation(project(":core"))
}

//tasks.register<Jar>("sourceJar") {
//    archiveClassifier.set("sources") // Results in your-app-sources.jar
//    from(sourceSets.main.get().java.srcDirs) // Include source directories
//    include("**/*.java") // Include .java files
//    include("**/*.kt")   // Include .kt files (if using Kotlin)
//}
//
//// Make the build task depend on sourceJar
//tasks.build {
//    dependsOn("sourceJar")
//}

tasks.register<Jar>("sourceJar") {
    archiveClassifier.set("sources") // Sets the classifier to "sources" (e.g., your-app-sources.jar)
    from(sourceSets.main.get().java.srcDirs) // Include source directories
    include("**/*.java") // Include .java files
    include("**/*.kt")   // Include .kt files (if using Kotlin)
    //? Include assets directory specifically?
}


val mainClassName = "de.tobi1craft.rapidtrack.teavm.TeaVMBuilder"
val mainConfigClassName = "de.tobi1craft.rapidtrack.teavm.TeaVMConfig"

tasks.register<JavaExec>("core-config") {
    group = "rapidtrack-teavm"
    description = "Config webapp"
    mainClass.set(mainConfigClassName)
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("core-build") {
    group = "rapidtrack-teavm"
    description = "Build teavm"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register("core-run-teavm") {
    group = "rapidtrack-teavm"
    description = "Run RapidTrack via TeaVM"
    val list = listOf("core-build", "jettyRun")
    dependsOn(list)

    tasks.findByName("jettyRun")?.mustRunAfter("core-build")
}


val main = "de.tobi1craft.rapidtrack.teavm.TeaVMLauncher"

teavm {
    js {
        obfuscated = false
        mainClass = main
        targetFileName = "app.js"
        relativePathInOutputDir = "webapp"
        outputDir = file("${layout.buildDirectory.get()}/dist")
        sourceMap = true
        debugInformation = true
        sourceFilePolicy = SourceFilePolicy.COPY

        devServer {
            port = 8080
        }
    }
    wasmGC {
        obfuscated = false
        mainClass = main
        targetFileName = "app.wasm"
        relativePathInOutputDir = "webapp"
        outputDir = file("${layout.buildDirectory.get()}/dist")
        sourceMap = true
        debugInformation = true
        sourceFilePolicy = SourceFilePolicy.COPY
    }
}
