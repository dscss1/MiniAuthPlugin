
repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.elytrium.net/repo/")
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.0.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.0.0-SNAPSHOT")
    implementation("org.jetbrains:annotations:24.1.0")
    implementation(project(":core"))
    compileOnly("net.elytrium.limboapi:api:1.1.24")
    implementation("net.elytrium:pcap:1.0.1")
}