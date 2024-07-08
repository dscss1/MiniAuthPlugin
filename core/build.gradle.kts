
plugins {
    id("net.kyori.blossom").version("2.1.0")
}

repositories {
    mavenCentral()
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/releases/")
}

dependencies {
    implementation("org.bspfsystems:yamlconfiguration:2.0.1")
//    compileOnly("net.kyori:adventure-api:4.9.3")
//    compileOnly("net.kyori:adventure-text-serializer-legacy:4.17.0")
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.5")
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")
    implementation("com.mysql:mysql-connector-j:8.4.0")
}

sourceSets {
    main {
        blossom {
            javaSources {
                property("version", project.version.toString())
                property("compiled_for", rootProject.ext["COMPILED_FOR"].toString())
            }
        }
    }
}