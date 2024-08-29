# Gradle Examples

This project contains Gradle Wrapper to use the correct version of Gradle to build the project.
As of this writing, this project works with Gradle 5.6.4 only

Each subproject is its own autonomous project that can be built independently. All subprojects are built together
using Gradle's composite builds.

Sub-projects:
 * brown-bag - Interesting tasks to build a scan of the build, also demonstrates task lifecycle. See build.gradle for examples
 * kotlinExample - A simple example of a Kotlin project with a hello world application that can be run from Gradle
 * swag-gen - A project that demonstrates how to generate Swagger JSON from server-side resources during the build
 * compA - Is a subproject that has a subproject itself
 * compB - is in a nested subdirectory

To see the interesting tasks in this project, check out the `build.gradle` and/or run:

```bash
./gradlew tasks
```

To run the brown-bag example:
```bash
cd brown-bag
../gradlew build
```

To run the Kotlin Hello World Example:
```bash
cd kotlinExample
../gradlew run
```

To see available tasks:
```bash
./gradlew tasks --all
```