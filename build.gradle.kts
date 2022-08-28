val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project
val postgres_version: String by project
val stripe_version: String by project
val docker_java_version: String by project

plugins {
	application
	kotlin("jvm") version "1.7.10"
	id("org.jetbrains.kotlin.plugin.serialization") version "1.7.10"
	id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "quozul.dev"
version = "0.0.1"
application {
	mainClass.set("io.ktor.server.netty.EngineMain")

	val isDevelopment: Boolean = project.ext.has("development")
	applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
	mavenCentral()
}

dependencies {
	// Ktor server
	implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-cors-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-default-headers-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
	implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-websockets:$ktor_version")
	implementation("ch.qos.logback:logback-classic:$logback_version")

	// Ktor client
	implementation("io.ktor:ktor-client-core:$ktor_version")
	implementation("io.ktor:ktor-client-cio:$ktor_version")
	implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
	implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

	// Exposed
	implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
	implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
	implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
	implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
	implementation("org.postgresql:postgresql:$postgres_version") // Exposed driver

	// Stripe
	implementation("com.stripe:stripe-java:$stripe_version")

	// Docker
	implementation("com.github.docker-java:docker-java-core:$docker_java_version")
	implementation("com.github.docker-java:docker-java-transport-httpclient5:$docker_java_version")

	// KotlinX
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
	implementation("org.jetbrains.kotlinx:kotlinx-datetime-jvm:0.4.0")

	// Mailing
	implementation("com.sun.mail:javax.mail:1.6.2")
}