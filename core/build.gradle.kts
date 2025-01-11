tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

dependencies {
    api("com.badlogicgames.gdx-controllers:gdx-controllers-core:${property("gdxControllersVersion")}")
    api("com.badlogicgames.gdx:gdx-bullet:${property("gdxVersion")}")
    api("com.badlogicgames.gdx:gdx:${property("gdxVersion")}")
    api("com.badlogicgames.gdx:gdx-freetype:${property("gdxVersion")}")
    api(project(":shared"))

    if (property("enableGraalNative") == "true") {
        implementation("io.github.berstanio:gdx-svmhelper-annotations:${property("graalHelperVersion")}")
    }
}
