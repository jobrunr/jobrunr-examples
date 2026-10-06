# JobRunr — Kotlin Example

A minimal HTTP server that demonstrates three core JobRunr background job patterns.

The app models a **newsletter subscription service**:

| Endpoint | Pattern | What happens |
|---|---|---|
| `POST /subscribe` | Fire-and-forget | Confirmation email sent as soon as a worker is free |
| `POST /confirm` | Delayed | Welcome email scheduled for execution 3 days later |
| *(on startup)* | Recurring | Weekly digest sent every Monday |

## Requirements

- Java 25 (JVM toolchain)
- Gradle (wrapper included)

## Project structure

```
kotlin-app/
├── src/main/kotlin/org/jobrunr/example/
│   ├── Main.kt               # HTTP server entry point + JobRunr setup
│   └── services/
│       └── EmailService.kt   # Simulated email jobs (prints to console)
├── src/test/kotlin/org/jobrunr/example/
│   ├── MainTest.kt           # Startup, /subscribe and /confirm behavior
│   └── services/
│       └── EmailServiceTest.kt
└── build.gradle.kts
```

## Running the app

```bash
git clone https://github.com/jobrunr/jobrunr-examples.git
cd jobrunr-examples/kotlin-app
./gradlew run
```

The server starts on **http://localhost:8080** and the JobRunr dashboard on **http://localhost:8000/dashboard**.

## Try it out

```bash
# Fire-and-forget: confirmation email sent immediately
curl -X POST "http://localhost:8080/subscribe?email=you@example.com"

# Delayed: welcome email scheduled 3 days from now
curl -X POST "http://localhost:8080/confirm?email=you@example.com"
```

## Running the tests

```bash
./gradlew test
```

The tests cover the `EmailService` jobs and the app behavior: the recurring `weekly-digest` job is registered on startup, `POST /subscribe` enqueues the confirmation email, and `POST /confirm` schedules the delayed welcome email.

## How it works

See the [Getting started with Kotlin](https://www.jobrunr.io/en/documentation/getting-started/kotlin/) guide for a full walkthrough of the code.
