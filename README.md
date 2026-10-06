# JobRunr Examples

Example projects showing how to integrate [JobRunr](https://www.jobrunr.io) background job scheduling into different JVM stacks.

## Projects

- [java-app](java-app/) — Plain Java 25
- [kotlin-app](kotlin-app/) — Kotlin + Kotlin Serialization
- [spring-app](spring-app/) — Spring Boot
- [quarkus-app](quarkus-app/) — Quarkus
- [micronaut-app](micronaut-app/) — Micronaut

See each project's README for setup instructions.

## Tests

Each project includes tests using the [JobRunr test fixtures](https://www.jobrunr.io/en/documentation/testing) (`org.jobrunr:jobrunr:...:test-fixtures`). Run them with the Gradle wrapper:

```bash
cd java-app && ./gradlew test
```

## Further reading

- [JobRunr documentation](https://www.jobrunr.io/en/documentation/)
- [Getting started guides](https://www.jobrunr.io/en/documentation/getting-started/)
