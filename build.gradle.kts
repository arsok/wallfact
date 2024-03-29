val ktorVersion = "1.6.4"
val kotlinVersion = "1.5.21"
val koinVersion = "3.1.2"
val logbackVersion = "1.4.0"
val groovyVersion = "3.0.12"
val mongoVersion = "4.3.0"
val commonsTextVersion = "1.9"

plugins {
    application
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.31"
}

group = "app.wallfact"
version = "0.5"
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
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")

    implementation("io.insert-koin:koin-ktor:$koinVersion")

    implementation("org.litote.kmongo:kmongo-coroutine:$mongoVersion")

    implementation("org.apache.commons:commons-text:$commonsTextVersion")
    implementation("org.codehaus.groovy:groovy:$groovyVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    testImplementation("io.insert-koin:koin-test:$koinVersion")
}

tasks.create("stage") {
    dependsOn("installDist")
}

tasks.compileKotlin {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xopt-in=kotlin.time.ExperimentalTime")
    }
}
