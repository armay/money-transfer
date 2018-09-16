group = "io.github.armay.moneytransfer"
version = "0.0.1"

val logbackVersion: String by extra { "1.2.3" }
val h2Version: String by extra { "1.4.197" }
val jdbiVersion: String by extra { "3.4.0" }
val javalinVersion: String by extra { "2.1.1" }
val junitVersion: String by extra { "5.2.0" }
val jacksonVersion: String by extra { "2.9.6" }
val unirestVersion: String by extra { "1.4.9" }

application {
    mainClassName = "io.github.armay.moneytransfer.MoneyTransferApp"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    tasks.withType<JavaCompile> {
        options.compilerArgs.add("-parameters")
    }
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform {
        includeEngines("junit-jupiter")
    }
}

plugins {
    application
    idea
    java
}

repositories {
    jcenter()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.h2database:h2:$h2Version")
    implementation("org.jdbi:jdbi3-core:$jdbiVersion")
    implementation("io.javalin:javalin:$javalinVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("org.bouncycastle:bcprov-jdk15on:1.60")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("com.mashape.unirest:unirest-java:$unirestVersion")
}
