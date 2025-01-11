plugins {
    java
    id("org.gretty") version "4.1.6"
}

gretty {
    contextPath = "/"
    extraResourceBase(file("build/dist/webapp"))
}

sourceSets["main"].resources.srcDirs.plusAssign(rootProject.file("assets"))
val mainClassName = "de.tobi1craft.rapidtrack.teavm.TeaVMBuilder"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${property("gdxVersion")}") //??? https://github.com/xpenatan/gdx-teavm/blob/master/examples/core/teavm/build.gradle

    implementation("com.github.xpenatan.gdx-teavm:backend-teavm:${property("gdxTeaVMVersion")}")
    implementation("com.github.xpenatan.gdx-teavm:gdx-freetype-teavm:${property("gdxTeaVMVersion")}")
    implementation("com.github.xpenatan.gdx-teavm:gdx-bullet-teavm:${property("gdxBulletTeaVMVersion")}")
//    implementation("org.teavm:teavm-classlib:${property("teaVMVersion")}")
//    implementation("org.teavm:teavm-core:${property("teaVMVersion")}")
//    implementation("org.teavm:teavm-jso-apis:${property("teaVMVersion")}")
//    implementation("org.teavm:teavm-jso-impl:${property("teaVMVersion")}")
//    implementation("org.teavm:teavm-jso:${property("teaVMVersion")}")
//    implementation("org.teavm:teavm-tooling:${property("teaVMVersion")}")
    implementation(project(":core"))
}

tasks.register<JavaExec>("buildJavaScript") {
    dependsOn(tasks.named("classes"))
    description = "Transpile bytecode to JavaScript via TeaVM"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
}
tasks.named("build").configure {
    dependsOn("buildJavaScript")
}

tasks.register<DefaultTask>("run") {
    description = "Run the JavaScript application hosted via a local Jetty server at http://localhost:8080/"
    dependsOn("buildJavaScript", tasks.named("jettyRun"))
}
