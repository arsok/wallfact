val ktorVersion: String by project
val kotlinVersion: String by project
val koinVersion = "3.1.2"

plugins {
    application
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.serialization") version "1.5.20"
}

group = "app.wallfact"
version = "0.0.1"
application {
    mainClass.set("app.wallfact.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    implementation("io.insert-koin:koin-ktor:$koinVersion")

    implementation( "io.ktor:ktor-client-serialization:$ktorVersion")

    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    testImplementation("io.insert-koin:koin-test:$koinVersion")
}

tasks.create("stage") {
    dependsOn("installDist")
}