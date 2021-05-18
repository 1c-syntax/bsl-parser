import java.net.URI
import java.util.*

plugins {
    maven
    idea
    jacoco
    java
    antlr
    id("com.github.hierynomus.license") version "0.15.0"
    id("org.sonarqube") version "3.2.0"
    id("com.github.gradle-git-version-calculator") version "1.1.0"
    id("com.github.ben-manes.versions") version "0.27.0"
    id("me.champeau.gradle.jmh") version "0.5.0"
}

repositories {
    mavenCentral()
    maven { url = URI("https://jitpack.io") }
}

group = "com.github.1c-syntax"
version = gitVersionCalculator.calculateVersion("v")

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

val antlrVersion = "4.7.4"
val antlrGroupId = "com.tunnelvisionlabs"
val antlrArtifactId = "antlr4"
val junitVersion = "5.6.0-RC1"

dependencies {
    compile(antlrGroupId, antlrArtifactId, antlrVersion)
    antlr(antlrGroupId, antlrArtifactId, antlrVersion)

    implementation("com.github.1c-syntax", "utils", "0.2.1")

    testImplementation("org.junit.jupiter", "junit-jupiter-api", junitVersion)
    testRuntime("org.junit.jupiter", "junit-jupiter-engine", junitVersion)
    testImplementation("org.assertj", "assertj-core", "3.14.0")

    // https://mvnrepository.com/artifact/commons-io/commons-io
    compile("commons-io", "commons-io", "2.6")
}

sourceSets {
    main {
        java.srcDirs("src/main/java", "src/main/gen")
        resources.srcDirs("src/main/resources")
    }
    test {
        java.srcDirs("src/test/java")
        resources.srcDirs("src/test/resources")
    }
}

sourceSets.jmh {
    java.srcDirs("src/main/jmh")
    resources.srcDirs("src/jmh/resources")
}

idea {
    module {
        // Marks the already(!) added srcDir as "generated"
        generatedSourceDirs = generatedSourceDirs + file("src/main/gen")
    }
}

jmh {
    jvmArgsAppend = listOf("-XX:+UseParallelGC")
    isIncludeTests = true
    duplicateClassesStrategy = DuplicatesStrategy.WARN
    timeUnit = "s"
}

tasks.generateGrammarSource {
    arguments = listOf(
            "-visitor",
            "-package",
            "com.github._1c_syntax.bsl.parser",
            "-encoding",
            "utf8"
    )
    outputDirectory = file("src/main/gen/com/github/_1c_syntax/bsl/parser")
}

tasks.generateGrammarSource {
    doLast {
        tasks.licenseFormatMain.get().actions[0].execute(tasks.licenseFormatMain.get())
    }
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
    }

    reports {
        html.isEnabled = true
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        xml.destination = File("$buildDir/reports/jacoco/test/jacoco.xml")
    }
}

license {
    header = rootProject.file("license/HEADER.txt")

    ext["year"] = "2018-" + Calendar.getInstance().get(Calendar.YEAR)
    ext["name"] = "Alexey Sosnoviy <labotamy@gmail.com>, Nikita Gryzlov <nixel2007@gmail.com>, Sergey Batanov <sergey.batanov@dmpas.ru>"
    ext["project"] = "BSL Parser"
    exclude("**/*.tokens")
    exclude("**/*.interp")
    exclude("**/*.g4")
    exclude("**/*.bsl")
    exclude("**/*.orig")
    exclude("**/*.gitkeep")
    strictCheck = true
    mapping("java", "SLASHSTAR_STYLE")
}

tasks.clean {
    doFirst {
        delete("src/main/gen", "out")
    }
}

sonarqube {
    properties {
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.organization", "1c-syntax")
        property("sonar.projectKey", "1c-syntax_bsl-parser")
        property("sonar.projectName", "BSL Parser")
        property("sonar.scm.exclusions.disabled", "true")
        property("sonar.issue.ignore.allfile", "// Generated from.*ANTLR")
        property("sonar.coverage.jacoco.xmlReportPaths", "$buildDir/reports/jacoco/test/jacoco.xml")
    }
}
