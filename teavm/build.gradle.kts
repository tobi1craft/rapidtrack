import org.teavm.gradle.api.SourceFilePolicy

plugins {
    java
    id("org.gretty") version "5.0.1"
    id("org.teavm") version "0.13.0"
}

gretty {
    contextPath = "/"
    servletContainer = "jetty11"
    extraResourceBase("build/dist/webapp")
}

//sourceSets["main"].resources.srcDirs.plusAssign(rootProject.file("assets"))

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${property("gdxVersion")}")
    implementation("com.github.xpenatan.gdx-teavm:backend-teavm:${property("gdxTeaVMVersion")}")
    implementation("com.github.xpenatan.gdx-teavm:gdx-freetype-teavm:${property("gdxTeaVMVersion")}")
    implementation("com.github.xpenatan.gdx-teavm:gdx-bullet-teavm:${property("gdxBulletTeaVMVersion")}")
    implementation(project(":core"))
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
    val list = listOf("core-build", "appRun")
    dependsOn(list)

    tasks.findByName("appRun")?.mustRunAfter("core-build")
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
