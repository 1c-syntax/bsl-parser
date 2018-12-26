import java.util.Calendar

plugins {
    maven
    idea
    jacoco
    java
    antlr
    id("com.github.hierynomus.license")
}

repositories {
    mavenCentral()
}

group = "org.github._1c_syntax"
version = "1.0"

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

dependencies {
    compile("org.antlr", "antlr4", "4.7.2")
    antlr("org.antlr", "antlr4", "4.7.2")

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
    arguments = arguments + "org.github._1c_syntax.parser"
    arguments = arguments + "-encoding"
    arguments = arguments + "utf8"
    outputDirectory = file("src/main/gen/org/github/_1c_syntax/parser")
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
        html.setEnabled(true)
    }
}

tasks.jacocoTestReport {
    reports {
        xml.setEnabled(true)
    }
}

license {
    header = rootProject.file("license/HEADER.txt")

    ext["year"] = Calendar.getInstance().get(Calendar.YEAR)
    ext["name"] = "Alexey Sosnoviy <labotamy@yandex.ru>, Nikita Gryzlov <nixel2007@gmail.com>, Sergey Batanov <sergey.batanov@dmpas.ru>"
    ext["project"] = "BSL Parser"
    strictCheck = true
    mapping("java", "SLASHSTAR_STYLE")
}

tasks.clean {
    doFirst {
        delete("src/main/gen", "out")
    }
}
