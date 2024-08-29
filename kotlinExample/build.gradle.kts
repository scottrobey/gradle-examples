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

val custom by configurations.creating {
    extendsFrom(configurations.testImplementation.get())
}

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
    custom("org.apache.httpcomponents:httpclient:4.5.6")
    custom("org.webjars:swagger-ui:2.1.5")
    custom(group = "org.apache.tomcat", name = "tomcat", version = "10.1.28", ext = "zip")

    testImplementation("junit", "junit", "4.12")
}

// Run Kotlin Hello World using:
// ../gradlew run
application {
    mainClassName = "org.sample.kotlinexample.HelloWorld"
}

distributions {
    getByName("main") {
        //distributionBaseName.set("kotlinexample")
        contents {
            from(zipTree(configurations.get("custom").filter { it.name.contains("tomcat") }.singleFile)) {
                into("extracted")
            }
            from(configurations.get("custom").filter { it.name.contains("swagger")}) {
                into("swagger")
            }
        }
    }
}
