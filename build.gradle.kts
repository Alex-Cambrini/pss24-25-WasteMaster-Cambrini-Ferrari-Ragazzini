plugins {
    // Java and application support
    java
    application

    // Code quality
    checkstyle

    // Runnable JAR support
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

checkstyle {
    toolVersion = "10.24.0"
}

repositories {
    mavenCentral()
}

// JavaFX configuration
val javaFXModules = listOf("base", "controls", "fxml", "swing", "graphics")
val supportedPlatforms = listOf("linux", "mac", "win")
val javaFxVersion = 21

dependencies {
    // SpotBugs annotations
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.9.2")

    // JavaFX dependencies
    for (platform in supportedPlatforms) {
        for (module in javaFXModules) {
            implementation("org.openjfx:javafx-$module:$javaFxVersion:$platform")
        }
    }

    // JUnit 5
    val jUnitVersion = "5.11.4"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitVersion")

    // Hibernate and persistence
    implementation("org.hibernate:hibernate-core:6.2.0.Final")
    implementation("org.hibernate.orm:hibernate-hikaricp:6.2.0.Final")
    implementation("org.hibernate.validator:hibernate-validator:7.0.0.Final")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.glassfish:jakarta.el:4.0.2")

    // Database drivers
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("com.zaxxer:HikariCP:5.1.0")
    testImplementation("com.h2database:h2:2.1.214")

    // Logging and utilities
    implementation("org.slf4j:slf4j-simple:2.0.9")
    implementation("org.mindrot:jbcrypt:0.4")

    // PDF generation
    implementation("com.github.librepdf:openpdf:1.3.30")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging.showStandardStreams = true
    jvmArgs = listOf(
        "-Djava.util.logging.config.file=${project.projectDir}/src/test/resources/logging.properties",
        "-Dorg.slf4j.simpleLogger.defaultLogLevel=off",
        "-Dorg.slf4j.simpleLogger.log.it.unibo.wastemaster=info"
    )
}

application {
    mainClass.set("it.unibo.wastemaster.main.App")
    applicationDefaultJvmArgs = listOf(
        "-Djava.util.logging.config.file=src/main/resources/logging.properties",
        "-Dorg.slf4j.simpleLogger.defaultLogLevel=warn"
    )
}
