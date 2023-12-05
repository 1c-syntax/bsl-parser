import java.util.*

plugins {
    `maven-publish`
    idea
    jacoco
    `java-library`
    antlr
    id("org.sonarqube") version "4.4.1.3373"
    id("org.cadixdev.licenser") version "0.6.1"
    id("com.github.gradle-git-version-calculator") version "1.1.0"
    id("io.freefair.javadoc-links") version "8.3"
    id("io.freefair.javadoc-utf-8") version "8.3"
    id("com.github.ben-manes.versions") version "0.48.0"
    id("me.champeau.gradle.jmh") version "0.5.3"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://jitpack.io")
}

group = "io.github.1c-syntax"
version = gitVersionCalculator.calculateVersion("v")

val antlrVersion = "4.9.0"
val antlrGroupId = "com.tunnelvisionlabs"
val antlrArtifactId = "antlr4"

dependencies {
    implementation(antlrGroupId, antlrArtifactId, antlrVersion)
    antlr(antlrGroupId, antlrArtifactId, antlrVersion)

    implementation("com.github.1c-syntax", "utils", "0.3.4")

    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.6.1")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.6.1")
    testImplementation("org.assertj", "assertj-core", "3.14.0")

    // https://mvnrepository.com/artifact/commons-io/commons-io
    implementation("commons-io", "commons-io", "2.6")

    // stat analysis
    compileOnly("com.google.code.findbugs", "jsr305", "3.0.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
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

// [bug] https://youtrack.jetbrains.com/issue/KT-46165
tasks.named<org.gradle.jvm.tasks.Jar>("sourcesJar") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.processTestResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
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
        tasks.updateLicenseMain.get().actions[0].execute(tasks.updateLicenseMain.get())
    }
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed", "standard_error")
    }

    reports {
        html.required.set(true)
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        xml.outputLocation.set(File("$buildDir/reports/jacoco/test/jacoco.xml"))
    }
}

tasks.javadoc {
    options {
        this as StandardJavadocDocletOptions
        links("https://javadoc.io/doc/org.antlr/antlr4-runtime/latest")
    }
}

license {
    header(rootProject.file("license/HEADER.txt"))
    newLine(false)
    ext["year"] = "2018-" + Calendar.getInstance().get(Calendar.YEAR)
    ext["name"] =
        "Alexey Sosnoviy <labotamy@gmail.com>, Nikita Fedkin <nixel2007@gmail.com>, Sergey Batanov <sergey.batanov@dmpas.ru>"
    ext["project"] = "BSL Parser"
    exclude("**/*.tokens")
    exclude("**/*.interp")
    exclude("**/*.g4")
    exclude("**/*.bsl")
    exclude("**/*.orig")
    exclude("**/*.gitkeep")
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

artifacts {
    archives(tasks["jar"])
    archives(tasks["sourcesJar"])
    archives(tasks["javadocJar"])
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                description.set("Collection of parsers for Language 1C (BSL) in ANTLR4 format.")
                url.set("https://github.com/1c-syntax/bsl-parser")
                licenses {
                    license {
                        name.set("GNU LGPL 3")
                        url.set("https://www.gnu.org/licenses/lgpl-3.0.txt")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("asosnoviy")
                        name.set("Alexey Sosnoviy")
                        email.set("labotamy@gmail.com")
                        url.set("https://github.com/asosnoviy")
                        organization.set("1c-syntax")
                        organizationUrl.set("https://github.com/1c-syntax")
                    }
                    developer {
                        id.set("nixel2007")
                        name.set("Nikita Fedkin")
                        email.set("nixel2007@gmail.com")
                        url.set("https://github.com/nixel2007")
                        organization.set("1c-syntax")
                        organizationUrl.set("https://github.com/1c-syntax")
                    }
                    developer {
                        id.set("dmpas")
                        name.set("Sergey Batanov")
                        email.set("sergey.batanov@dmpas.ru")
                        url.set("https://github.com/dmpas")
                        organization.set("1c-syntax")
                        organizationUrl.set("https://github.com/1c-syntax")
                    }
                    developer {
                        id.set("theshadowco")
                        name.set("Valery Maximov")
                        email.set("maximovvalery@gmail.com")
                        url.set("https://github.com/theshadowco")
                        organization.set("1c-syntax")
                        organizationUrl.set("https://github.com/1c-syntax")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/1c-syntax/bsl-parser.git")
                    developerConnection.set("scm:git:git@github.com:1c-syntax/bsl-parser.git")
                    url.set("https://github.com/1c-syntax/bsl-parser")
                }
            }
        }
    }
}
