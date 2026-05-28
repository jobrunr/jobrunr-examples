# JobRunr — Java Example

A minimal HTTP server that demonstrates three core JobRunr background job patterns.

The app models a **newsletter subscription service**:

| Endpoint | Pattern | What happens |
|---|---|---|
| `POST /subscribe` | Fire-and-forget | Confirmation email sent as soon as a worker is free |
| `POST /confirm` | Delayed | Welcome email scheduled for execution 3 days later |
| *(on startup)* | Recurring | Weekly digest sent every Monday |

## Requirements

- Java 25 (uses compact source files)
- Gradle (wrapper included)

## Project structure

```
java-app/
├── src/main/java/org/jobrunr/example/
│   ├── App.java              # HTTP server entry point + JobRunr setup
│   └── services/
│       └── EmailService.java # Simulated email jobs (prints to console)
└── build.gradle
```

## Running the app

```bash
git clone https://github.com/jobrunr/jobrunr-examples.git
cd jobrunr-examples/java-app
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

## How it works

See the [Getting started with Java](https://www.jobrunr.io/en/documentation/getting-started/java/) guide for a full walkthrough of the code.