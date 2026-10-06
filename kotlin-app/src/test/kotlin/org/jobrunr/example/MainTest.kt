package org.jobrunr.example

import org.assertj.core.api.Assertions.assertThat
import org.jobrunr.configuration.JobRunr
import org.jobrunr.example.services.EmailService
import org.jobrunr.jobs.JobAssert
import org.jobrunr.jobs.JobId
import org.jobrunr.jobs.RecurringJobAssert
import org.jobrunr.scheduling.cron.Cron
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.DayOfWeek

class MainTest {

    companion object {
        private const val BASE_URL = "http://localhost:8080"
        private val HTTP: HttpClient = HttpClient.newHttpClient()

        @JvmStatic
        @BeforeAll
        fun startApp() {
            main()
        }

        @JvmStatic
        @AfterAll
        fun stopApp() {
            JobRunr.destroy()
        }

        private fun post(path: String): HttpResponse<String> {
            val request = HttpRequest.newBuilder(URI.create(BASE_URL + path))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build()
            return HTTP.send(request, HttpResponse.BodyHandlers.ofString())
        }

        private fun jobIdFrom(response: HttpResponse<String>): JobId {
            val body = response.body()
            val index = body.lastIndexOf("job id ")
            check(index >= 0) { "No job id in response: $body" }
            return JobId.parse(body.substring(index + "job id ".length).trim())
        }
    }

    @Test
    fun recurringJobIsCreatedAfterStartup() {
        val recurringJob = JobRunr.getStorageProvider().getRecurringJobs()
            .first { "weekly-digest" == it.id }

        RecurringJobAssert.assertThat(recurringJob)
            .hasId("weekly-digest")
            .hasScheduleExpression(Cron.weekly(DayOfWeek.MONDAY))
            .hasJobDetails(EmailService::class.java, "sendWeeklyDigest")
    }

    @Test
    fun subscribeEndpointEnqueuesConfirmationEmail() {
        val email = "alice@example.com"

        val response = post("/subscribe?email=$email")

        assertThat(response.statusCode()).isEqualTo(202)
        val job = JobRunr.getStorageProvider().getJobById(jobIdFrom(response))
        JobAssert.assertThat(job)
            .hasJobDetails(EmailService::class.java, "sendConfirmation", email)
    }

    @Test
    fun confirmEndpointSchedulesWelcomeEmail() {
        val email = "bob@example.com"

        val response = post("/confirm?email=$email")

        assertThat(response.statusCode()).isEqualTo(202)
        val job = JobRunr.getStorageProvider().getJobById(jobIdFrom(response))
        JobAssert.assertThat(job)
            .hasJobDetails(EmailService::class.java, "sendWelcome", email)
    }
}
