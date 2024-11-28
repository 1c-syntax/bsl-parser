import me.qoomon.gitversioning.commons.GitRefType
import java.util.*

plugins {
    `maven-publish`
    idea
    jacoco
    `java-library`
    antlr
    signing
    id("org.sonarqube") version "6.0.0.5145"
    id("org.cadixdev.licenser") version "0.6.1"
    id("me.qoomon.git-versioning") version "6.4.4"
    id("io.freefair.javadoc-links") version "8.11"
    id("io.freefair.javadoc-utf-8") version "8.11"
    id("com.github.ben-manes.versions") version "0.51.0"
    id("me.champeau.gradle.jmh") version "0.5.3"
    id("io.freefair.maven-central.validate-poms") version "8.11"
    id("ru.vyarus.pom") version "3.0.0"
    id("io.codearte.nexus-staging") version "0.30.0"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://jitpack.io")
}

group = "io.github.1c-syntax"
gitVersioning.apply {
    refs {
        considerTagsOnBranches = true
        tag("v(?<tagVersion>[0-9].*)") {
            version = "\${ref.tagVersion}\${dirty}"
        }
        branch(".+") {
            version = "\${ref}-\${commit.short}\${dirty}"
        }
    }

    rev {
        version = "\${commit.short}\${dirty}"
    }
}
val isSnapshot = gitVersioning.gitVersionDetails.refType != GitRefType.TAG

dependencies {
    antlr("org.antlr", "antlr4", "4.13.1")

    implementation("io.github.1c-syntax", "bsl-parser-core", "0.2.0")

    // stat analysis
    compileOnly("com.google.code.findbugs", "jsr305", "3.0.2")

    // testing
    testImplementation("io.github.1c-syntax", "bsl-parser-testing", "0.2.0")
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.10.1")
    testImplementation("org.junit.jupiter", "junit-jupiter-engine", "5.10.1")
    testImplementation("org.junit.jupiter", "junit-jupiter-params", "5.10.1")
    testImplementation("org.assertj", "assertj-core", "3.25.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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
    dependsOn(tasks.generateGrammarSource)
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

tasks.updateLicenseMain {
    mustRunAfter(tasks.generateGrammarSource)
}

tasks.checkLicenseMain {
    dependsOn(tasks.updateLicenseMain)
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

sonar {
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

signing {
    val signingInMemoryKey: String? by project      // env.ORG_GRADLE_PROJECT_signingInMemoryKey
    val signingInMemoryPassword: String? by project // env.ORG_GRADLE_PROJECT_signingInMemoryPassword
    if (signingInMemoryKey != null) {
        useInMemoryPgpKeys(signingInMemoryKey, signingInMemoryPassword)
        sign(publishing.publications)
    }
}

publishing {
    repositories {
        maven {
            name = "sonatype"
            url = if (isSnapshot)
                uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            else
                uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")

            val sonatypeUsername: String? by project
            val sonatypePassword: String? by project

            credentials {
                username = sonatypeUsername // ORG_GRADLE_PROJECT_sonatypeUsername
                password = sonatypePassword // ORG_GRADLE_PROJECT_sonatypePassword
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            if (isSnapshot && project.hasProperty("simplifyVersion")) {
                version = findProperty("git.ref.slug") as String + "-SNAPSHOT"
            }

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

nexusStaging {
    serverUrl = "https://s01.oss.sonatype.org/service/local/"
    stagingProfileId = "15bd88b4d17915" // ./gradlew getStagingProfile
}
