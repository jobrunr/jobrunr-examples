package org.jobrunr.example

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import org.jobrunr.configuration.JobRunr
import org.jobrunr.example.services.EmailService
import org.jobrunr.scheduling.BackgroundJob
import org.jobrunr.scheduling.cron.Cron
import org.jobrunr.storage.InMemoryStorageProvider
import java.net.InetSocketAddress
import java.time.DayOfWeek
import java.time.Instant
import java.time.temporal.ChronoUnit

fun main() {
    val emailService = EmailService()

    JobRunr.configure()  // Using JobRunr Pro? Replace `JobRunr` with `JobRunrPro`
        .useStorageProvider(InMemoryStorageProvider())
        .useBackgroundJobServer()
        .useDashboard()
        .initialize()

    BackgroundJob.scheduleRecurrently("weekly-digest", Cron.weekly(DayOfWeek.MONDAY)) {
        emailService.sendWeeklyDigest()
    }

    val server = HttpServer.create(InetSocketAddress(8080), 0)

    server.createContext("/subscribe") { exchange ->
        if (exchange.requestMethod != "POST") { exchange.respond(405, "Method Not Allowed"); return@createContext }
        val email = exchange.getEmailFromQueryParams()
            ?: run { exchange.respond(400, "Missing email parameter"); return@createContext }
        val jobId = BackgroundJob.enqueue { emailService.sendConfirmation(email) }
        exchange.respond(202, "Confirmation email queued with job id $jobId")
    }

    server.createContext("/confirm") { exchange ->
        if (exchange.requestMethod != "POST") { exchange.respond(405, "Method Not Allowed"); return@createContext }
        val email = exchange.getEmailFromQueryParams()
            ?: run { exchange.respond(400, "Missing email parameter"); return@createContext }
        val jobId = BackgroundJob.schedule(Instant.now().plus(3, ChronoUnit.DAYS)) {
            emailService.sendWelcome(email)
        }
        exchange.respond(202, "Welcome email scheduled with job id $jobId")
    }

    server.start()
    println("Listening on http://localhost:8080")
}

fun HttpExchange.getEmailFromQueryParams(): String? {
    val q = requestURI.query ?: return null
    val value = q.substringAfter("email=", missingDelimiterValue = "")
    return value.ifEmpty { null }
}

fun HttpExchange.respond(status: Int, body: String) {
    val bytes = body.toByteArray()
    sendResponseHeaders(status, bytes.size.toLong())
    responseBody.write(bytes)
    close()
}
