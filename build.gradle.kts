plugins {
    idea
    java
}

buildscript {
    repositories {
        mavenCentral()
        maven(url = "https://s01.oss.sonatype.org")
        gradlePluginPortal()
        mavenLocal()
        google()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven(url = "https://jitpack.io")
    }
    dependencies {

    }
}

allprojects {
    apply(plugin = "idea")

    // This allows you to "Build and run using IntelliJ IDEA", an option in IDEA's Settings.

    idea {
        module {
            outputDir = file("build/classes/java/main")
            testOutputDir = file("build/classes/java/test")
        }
    }
}

configure(subprojects) {
    apply(plugin = "java-library")
    java.sourceCompatibility = JavaVersion.VERSION_23

    // From https://lyze.dev/2021/04/29/libGDX-Internal-Assets-List/
    // The article can be helpful when using assets.txt in your project.
    tasks.register("generateAssetList") {
        val assetsFolder = File("${project.rootDir}/assets/")
        val assetsFile = File(assetsFolder, "assets.txt")

        inputs.dir(assetsFolder)

        doLast {
            // delete that file in case we've already created it
            assetsFile.delete()

            // iterate through all files inside that folder,
            // convert it to a relative path
            // and append it to the file assets.txt
            fileTree(assetsFolder).map { assetsFolder.toPath().relativize(it.toPath()).toString() }
                .sorted()
                .forEach { assetsFile.appendText(it + "\n") }
        }
    }

    tasks.named("processResources") {
        dependsOn("generateAssetList")
    }

    tasks.named<JavaCompile>("compileJava") {
        options.isIncremental = true
    }
}

subprojects {
    version = project.property("projectVersion") as String
    extra["appName"] = "rapidtrack"
    extra["humanName"] = "RapidTrack"

    repositories {
        mavenCentral()
        maven(url = "https://s01.oss.sonatype.org")
        // You may want to remove the following line if you have errors downloading dependencies.
        mavenLocal()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven(url = "https://jitpack.io")
        maven(url = "https://teavm.org/maven/repository/")
    }
}
