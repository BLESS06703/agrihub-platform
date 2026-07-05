plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("io.ktor.plugin") version "3.0.0"
    id("org.jetbrains.kotlin.plugin.jpa") version "2.0.0"
    application
}

group = "com.agrihub.platform"
version = "1.0.0"

application {
    mainClass.set("com.agrihub.platform.app.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor
    implementation("io.ktor:ktor-server-core:3.0.0")
    implementation("io.ktor:ktor-server-netty:3.0.0")
    implementation("io.ktor:ktor-server-content-negotiation:3.0.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0")
    implementation("io.ktor:ktor-server-auth:3.0.0")
    implementation("io.ktor:ktor-server-auth-jwt:3.0.0")
    implementation("io.ktor:ktor-server-status-pages:3.0.0")
    implementation("io.ktor:ktor-server-call-logging:3.0.0")
    implementation("io.ktor:ktor-server-cors:3.0.0")
    implementation("io.ktor:ktor-server-compression:3.0.0")
    
    // Exposed ORM
    implementation("org.jetbrains.exposed:exposed-core:0.50.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.50.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.50.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.50.0")
    
    // PostgreSQL
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.zaxxer:HikariCP:5.1.0")
    
    // Redis
    implementation("io.lettuce:lettuce-core:6.3.2")
    
    // Security
    implementation("de.mkammerer:argon2-jvm:2.11")
    implementation("com.auth0:java-jwt:4.4.0")
    
    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.6")
    
    // Testing
    testImplementation("io.ktor:ktor-server-tests:3.0.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:2.0.0")
    testImplementation("org.testcontainers:postgresql:1.19.8")
}

kotlin {
    jvmToolchain(21)
}
