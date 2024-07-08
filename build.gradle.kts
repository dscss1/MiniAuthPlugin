/*

SURVIVALBOOM NETWORK 2024 | SURVIVALBOOM MODERATION
        INTERNAL USE ONLY | BY TIMURishche

 */

group = "ua.dscss2.miniauthplugin"
version = "1.0"

ext["COMPILED_FOR"] = "SurvivalBoom Network 2024. Internal Use Only."

val targetJava = 17

plugins {
    java
}

subprojects {

    apply(plugin = "java")

    version = rootProject.version
    group = rootProject.group

    tasks.withType(JavaCompile::class) {
        options.release.set(targetJava)
        options.encoding = "UTF-8"
    }
}

dependencies {
    subprojects.forEach { project: Project ->
        implementation(project)
    }
}

tasks.jar {

    destinationDirectory.set(rootProject.projectDir)

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    subprojects.forEach { subproject ->
        from(subproject.sourceSets.main.get().output)
        subproject.configurations.runtimeClasspath.get().forEach { file ->
            if (file.isDirectory) from(file)
            else from(zipTree(file))
        }
    }

    println("--- --- --- ---")
    println(String.format("OUTPUT JAR PATH: %s", destinationDirectory.get().asFile.absolutePath))
    println("--- --- --- ---")

}

tasks.withType(JavaCompile::class) {
    options.release.set(targetJava)
    options.encoding = "UTF-8"
}