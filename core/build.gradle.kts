dependencies {
    api(project(":shared"))
    api("com.badlogicgames.gdx-controllers:gdx-controllers-core:${property("gdxControllersVersion")}")
    api("com.badlogicgames.gdx:gdx-bullet:${property("gdxVersion")}")
    api("com.badlogicgames.gdx:gdx:${property("gdxVersion")}")
    api("com.badlogicgames.gdx:gdx-freetype:${property("gdxVersion")}")
    api("com.github.mgsx-dev.gdx-gltf:gltf:${property("gltfVersion")}")

    if (property("enableGraalNative") == "true") {
        implementation("io.github.berstanio:gdx-svmhelper-annotations:${property("graalHelperVersion")}")
    }
}
