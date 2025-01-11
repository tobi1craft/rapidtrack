import io.github.fourlastor.construo.Target
import java.util.*

buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("io.github.fourlastor:construo:1.5.1")
        if (property("enableGraalNative") == "true") {
            classpath("org.graalvm.buildtools.native:org.graalvm.buildtools.native.gradle.plugin:0.10.4")
        }
    }
}

plugins {
    application
    id("io.github.fourlastor.construo") version "1.5.1"
    id("org.graalvm.buildtools.native") version "0.10.4"
}

sourceSets["main"].resources.srcDirs.plusAssign(rootProject.file("assets"))

val mainClassName = "de.tobi1craft.rapidtrack.lwjgl3.Lwjgl3Launcher"
application {
    mainClass.set(mainClassName)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

if (JavaVersion.current().isJava9Compatible) {
    tasks.withType<JavaCompile> {
        options.release.set(21)
    }
}

dependencies {
    implementation("com.badlogicgames.gdx-controllers:gdx-controllers-desktop:${property("gdxControllersVersion")}")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:${property("gdxVersion")}")
    implementation("com.badlogicgames.gdx:gdx-bullet-platform:${property("gdxVersion")}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-platform:${property("gdxVersion")}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:${property("gdxVersion")}:natives-desktop")
    implementation(project(":core"))

    if (property("enableGraalNative") == "true") {
        implementation("io.github.berstanio:gdx-svmhelper-backend-lwjgl3:${property("graalHelperVersion")}")
        implementation("io.github.berstanio:gdx-svmhelper-extension-bullet:${property("graalHelperVersion")}")
    }
}

val os = System.getProperty("os.name").lowercase(Locale.getDefault())

tasks.named<JavaExec>("run") {
    outputs.upToDateWhen { false }
    workingDir = rootProject.file("assets")
    isIgnoreExitValue = true
    if (os.contains("mac")) {
        jvmArgs!!.add("-XstartOnFirstThread")
    }
}

tasks.named<Jar>("jar") {
    archiveFileName.set("${project.extra["humanName"] as String}-${project.property("projectVersion")}.jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn(configurations.runtimeClasspath)
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    exclude("META-INF/INDEX.LIST", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    dependencies {
        exclude("META-INF/INDEX.LIST", "META-INF/maven/**", "windows/x86/**")
    }
    manifest {
        attributes["Main-Class"] = mainClassName
    }
    doLast {
        file(archiveFile).setExecutable(true, false)
    }
}

construo {
    //TODO: include assets
    name.set(project.extra["appName"] as String)
    humanName.set(project.extra["humanName"] as String)
    //version.set(project.property("projectVersion"))

    targets {
        create<Target.Linux>("linuxX64") {
            architecture.set(Target.Architecture.X86_64)
            //jdkUrl.set("https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.12%2B7/OpenJDK17U-jdk_x64_linux_hotspot_17.0.12_7.tar.gz")
            jdkUrl.set("https://github.com/adoptium/temurin23-binaries/releases/download/jdk-23.0.1%2B11/OpenJDK23U-jdk_x64_linux_hotspot_23.0.1_11.tar.gz")
        }
        create<Target.MacOs>("macM1") {
            architecture.set(Target.Architecture.AARCH64)
            //jdkUrl.set("https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.12%2B7/OpenJDK17U-jdk_aarch64_mac_hotspot_17.0.12_7.tar.gz")
            jdkUrl.set("https://github.com/adoptium/temurin23-binaries/releases/download/jdk-23.0.1%2B11/OpenJDK23U-jdk_aarch64_mac_hotspot_23.0.1_11.tar.gz")
            identifier.set("de.tobi1craft.rapidtrack.${project.extra["appName"] as String}")
            macIcon.set(project.file("icons/logo.icns"))
        }
        create<Target.MacOs>("macX64") {
            architecture.set(Target.Architecture.X86_64)
            //jdkUrl.set("https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.12%2B7/OpenJDK17U-jdk_x64_mac_hotspot_17.0.12_7.tar.gz")
            jdkUrl.set("https://github.com/adoptium/temurin23-binaries/releases/download/jdk-23.0.1%2B11/OpenJDK23U-jdk_x64_mac_hotspot_23.0.1_11.tar.gz")
            identifier.set("de.tobi1craft.rapidtrack.${project.extra["appName"] as String}")
            macIcon.set(project.file("icons/logo.icns"))
        }
        create<Target.Windows>("winX64") {
            architecture.set(Target.Architecture.X86_64)
            //jdkUrl.set("https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.12%2B7/OpenJDK17U-jdk_x64_windows_hotspot_17.0.12_7.zip")
            jdkUrl.set("https://github.com/adoptium/temurin23-binaries/releases/download/jdk-23.0.1%2B11/OpenJDK23U-jdk_x64_windows_hotspot_23.0.1_11.zip")
        }
    }
}


tasks.register("dist") {
    dependsOn("jar")
}

distributions {
    main {
        contents {
            into("libs") {
                from(project.configurations.runtimeClasspath) {
                    exclude(project.tasks.jar.get().archiveFileName.get())
                }
            }
        }
    }
}

tasks.named<CreateStartScripts>("startScripts") {
    dependsOn(tasks.jar)
    classpath = files(tasks.jar.get().archiveFile)
}



if (property("enableGraalNative") == "true") {
    project(":lwjgl3") {

        graalvmNative {
            binaries {
                named("main") {
                    imageName.set(project.extra["appName"] as String)
                    mainClass.set(mainClassName)
                    buildArgs.add("-march=compatibility")
                    jvmArgs.addAll("-Dfile.encoding=UTF8")
                    sharedLibrary.set(false)
                    resources.autodetect()
                }
            }
        }

        tasks.named<JavaExec>("run") {
            doNotTrackState("Running the app should not be affected by Graal.")
        }

        tasks.named("generateResourcesConfigFile").configure {
            doFirst {
                val assetsFolder = File("${project.rootDir}/assets/")
                val lwjgl3 = project(":lwjgl3")
                val resFolder =
                    File("${lwjgl3.projectDir}/src/main/resources/META-INF/native-image/${project.extra["appName"]}")
                resFolder.mkdirs()
                val resFile = File(resFolder, "resource-config.json")
                resFile.delete()
                resFile.appendText(
                    """{
  "resources":{
  "includes":[
    {
      "pattern": ".*("""
                )
                assetsFolder.walkTopDown().forEach {
                    resFile.appendText("\\\\Q${it.name}\\\\E|")
                }
                resFile.appendText(
                    """libgdx.+\\\\.png|lsans.+)"
    }
  ]},
  "bundles":[]
}"""
                )
            }
        }
    }
}

