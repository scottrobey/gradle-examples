/**
 * See: https://github.com/gradle/kotlin-dsl
 *
 * Waiting for other documentation, for Gradle plugins, to have both a groovy and kotlin examples, some do, but most
 * that I've found don't
 */

plugins {
    java
    application
    distribution
}

group = "org.sample.kotlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    runtime("org.apache.httpcomponents:httpclient:4.5.6")
    runtime("org.webjars:swagger-ui:2.1.5")
    runtime(group = "tomcat", name = "apache-tomcat", version = "5.5.23", ext = "zip")

    testCompile("junit", "junit", "4.12")
}


application {
    mainClassName = "org.sample.kotlinexample.HellowWorld"
}

distributions {
    getByName("main") {
        baseName = "kotlinexample"
        contents {
            from(zipTree(configurations.runtime.filter { it.name.contains("tomcat") }.singleFile)) {
                into("extracted")
            }
            from(configurations.runtime.filter { it.name.contains("swagger")}) {
                into("swagger")
            }
        }
    }
}
