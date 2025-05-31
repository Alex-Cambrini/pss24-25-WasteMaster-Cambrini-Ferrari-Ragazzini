
plugins {
    // Apply the java plugin to add support for Java
    java

    // Apply the application plugin to add support for building a CLI application
    // You can run your app via task "run": ./gradlew run
    application
    checkstyle

    /*
     * Adds tasks to export a runnable jar
     * In order to create it, launch the "shadowJar" task.
     * The runnable jar will be found in build/libs/projectname-all.jar
     */
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

checkstyle {
    toolVersion = "10.24.0"
}


repositories {
    mavenCentral()
}

val javaFXModules = listOf(
    "base",
    "controls",
    "fxml",
    "swing",
    "graphics"
)

val supportedPlatforms = listOf("linux", "mac", "win") // All required for OOP

dependencies {
    // Suppressions for SpotBugs
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.9.2")

    // Example library: Guava. Add what you need (and remove Guava if you don't use it)
    // implementation("com.google.guava:guava:28.1-jre")

    // JavaFX: comment out if you do not need them
    val javaFxVersion = 15
    for (platform in supportedPlatforms) {
        for (module in javaFXModules) {
            implementation("org.openjfx:javafx-$module:$javaFxVersion:$platform")
        }
    }

    val jUnitVersion = "5.11.4"
    // JUnit API and testing engine
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitVersion")
    



    // Hibernate Core per la gestione della persistenza
    implementation("org.hibernate:hibernate-core:6.2.0.Final")
    implementation("org.hibernate.orm:hibernate-hikaricp:6.2.0.Final")

    // Hibernate Validator
    implementation ("org.hibernate.validator:hibernate-validator:7.0.0.Final")
    // Jakarta Bean Validation API
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    // Jakarta Persistence API
    implementation("org.glassfish:jakarta.el:4.0.2")


    // Driver JDBC per MySQL
    implementation("mysql:mysql-connector-java:8.0.33")

    // HikariCP per la gestione del pool di connessioni
    implementation("com.zaxxer:HikariCP:5.1.0")

    implementation("org.slf4j:slf4j-simple:2.0.9")

    implementation("org.mindrot:jbcrypt:0.4")

    // Dipendenza H2 per il database in memoria durante i test
    testImplementation("com.h2database:h2:2.1.214")



}




tasks.withType<Test> {
    // Enables JUnit 5 Jupiter module
    useJUnitPlatform()
    testLogging {
		showStandardStreams = true
	}
    jvmArgs = listOf(
		"-Djava.util.logging.config.file=${project.projectDir}/src/test/resources/logging.properties",
		"-Dorg.slf4j.simpleLogger.defaultLogLevel=off",
		"-Dorg.slf4j.simpleLogger.log.it.unibo.wastemaster=info"
	)
}

application {
    // Define the main class for the application
    mainClass.set("it.unibo.wastemaster.main.App")
    applicationDefaultJvmArgs = listOf(
        "-Djava.util.logging.config.file=src/main/resources/logging.properties",
        "-Dorg.slf4j.simpleLogger.defaultLogLevel=warn"
    )
}
