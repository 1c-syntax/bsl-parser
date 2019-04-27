import java.net.URI
import java.util.*

plugins {
    maven
    idea
    jacoco
    java
    antlr
    id("com.github.hierynomus.license") version "0.14.0"
    id("org.sonarqube") version "2.6.2"
    id("com.github.gradle-git-version-calculator") version "1.1.0"
}

repositories {
    mavenCentral()
    maven { url = URI("https://jitpack.io") }
}

group = "org.github._1c_syntax"
version = gitVersionCalculator.calculateVersion("v")

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

dependencies {
    compile("com.github.nixel2007", "antlr4", "798213b0735dd8c803b5b7363cedb4e65ab936d8")
    antlr("com.github.nixel2007", "antlr4", "798213b0735dd8c803b5b7363cedb4e65ab936d8")

    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.2.0")
    testRuntime("org.junit.jupiter", "junit-jupiter-engine", "5.2.0")

    // https://mvnrepository.com/artifact/commons-io/commons-io
    testImplementation("commons-io", "commons-io", "2.6")
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

idea {
    module {
        // Marks the already(!) added srcDir as "generated"
        generatedSourceDirs = generatedSourceDirs + file("src/main/gen")
    }
}

tasks.generateGrammarSource {
    arguments = arguments + "-visitor"
    arguments = arguments + "-package"
    arguments = arguments + "org.github._1c_syntax.bsl.parser"
    arguments = arguments + "-encoding"
    arguments = arguments + "utf8"
    outputDirectory = file("src/main/gen/org/github/_1c_syntax/bsl/parser")
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
        property("sonar.coverage.jacoco.xmlReportPaths", "$buildDir/reports/jacoco/test/jacoco.xml")
    }
}
