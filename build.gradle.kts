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

subprojects {
    apply(plugin = "java-library")

    version = project.property("projectVersion") as String
    extra["displayName"] = "RapidTrack"

    val subprojectName = name
    val assetsSourceDirs = mutableListOf<File>()
    val assetsTargetDir = file("src/main/assets")

    when (subprojectName) {
        "core", "lwjgl3", "teavm" -> {
            assetsSourceDirs.add(file("$rootDir/assets/core"))
            assetsSourceDirs.add(file("$rootDir/assets/shared"))
        }
        "server" -> {
            assetsSourceDirs.add(file("$rootDir/assets/server"))
            assetsSourceDirs.add(file("$rootDir/assets/shared"))
        }
        "shared" -> assetsSourceDirs.add(file("$rootDir/assets/shared"))
        else -> println("Unknown subproject: $subprojectName")
    }

    tasks.register<Sync>("syncAssets") {
        if(!assetsTargetDir.exists()) assetsTargetDir.mkdir()
        into(assetsTargetDir)

        assetsSourceDirs.forEach { sourceDir ->
            if (sourceDir.exists()) {
                from(sourceDir)
            } else {
                println("Source directory not found: $sourceDir for subproject: $subprojectName")
            }
        }
    }

    tasks.register<Delete>("deleteSyncedAssets") {
        if(assetsTargetDir.exists()) delete(assetsTargetDir)
    }

    repositories {
        mavenCentral()
        maven(url = "https://s01.oss.sonatype.org")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven(url = "https://jitpack.io")
        maven(url = "https://teavm.org/maven/repository/")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }

    sourceSets {
        main {
            resources {
                srcDirs("src/main/assets")
            }
        }
    }

    // From https://lyze.dev/2021/04/29/libGDX-Internal-Assets-List/
    // The article can be helpful when using assets.txt in your project.
    tasks.register("generateAssetList") {
        dependsOn("syncAssets")
        val assetsFolder = file("src/main/assets/")
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

    tasks.named<ProcessResources>("processResources") {
        dependsOn("syncAssets")
        dependsOn("generateAssetList")
    }

    tasks.named<JavaCompile>("compileJava") {
        options.isIncremental = true
        options.encoding = "UTF-8"
    }

    tasks.named<Delete>("clean") {
        dependsOn("deleteSyncedAssets")
    }
}
