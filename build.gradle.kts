val ktorVersion = "1.6.2"
val kotlinVersion = "1.5.21"
val koinVersion = "3.1.2"
val logbackVersion = "1.2.3"
val groovyVersion = "3.0.8"
val mongoVersion = "4.2.8"

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

    implementation("org.litote.kmongo:kmongo-coroutine:$mongoVersion")

    implementation("org.codehaus.groovy:groovy:$groovyVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    testImplementation("io.insert-koin:koin-test:$koinVersion")
}

tasks.create("stage") {
    dependsOn("installDist")
}