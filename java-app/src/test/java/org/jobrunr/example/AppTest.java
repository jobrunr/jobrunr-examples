package org.jobrunr.example;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.example.services.EmailService;
import org.jobrunr.jobs.Job;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.RecurringJob;
import org.jobrunr.scheduling.cron.Cron;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.support.ReflectionSupport;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.DayOfWeek;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jobrunr.jobs.JobAssert.assertThat;
import static org.jobrunr.jobs.RecurringJobAssert.assertThat;

class AppTest {

    private static final String BASE_URL = "http://localhost:8080";
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    @BeforeAll
    static void startApp() throws Exception {
        // setup is a bit complex due to use of a compact file, your production app probably doesn't need this
        Class<?> app = Class.forName("App");
        ReflectionSupport.invokeMethod(app.getDeclaredMethod("main"), ReflectionSupport.newInstance(app));
    }

    @AfterAll
    static void stopApp() {
        JobRunr.destroy();
    }

    @Test
    void recurringJobIsCreatedAfterStartup() {
       RecurringJob recurringJob = JobRunr.getStorageProvider().getRecurringJobs()
                .stream()
                .filter(rj -> "weekly-digest".equals(rj.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(recurringJob)
                .hasId("weekly-digest")
                .hasScheduleExpression(Cron.weekly(DayOfWeek.MONDAY))
                .hasJobDetails(EmailService.class, "sendWeeklyDigest");
    }

    @Test
    void subscribeEndpointEnqueuesConfirmationEmail() throws Exception {
        String email = "alice@example.com";

        HttpResponse<String> response = post("/subscribe?email=" + email);

        assertThat(response.statusCode()).isEqualTo(202);
        Job job = JobRunr.getStorageProvider().getJobById(jobIdFrom(response));
        assertThat(job)
                .hasJobDetails(EmailService.class, "sendConfirmation", email);
    }

    @Test
    void confirmEndpointSchedulesWelcomeEmail() throws Exception {
        String email = "bob@example.com";

        HttpResponse<String> response = post("/confirm?email=" + email);

        assertThat(response.statusCode()).isEqualTo(202);
        Job job = JobRunr.getStorageProvider().getJobById(jobIdFrom(response));
        assertThat(job)
                .hasJobDetails(EmailService.class, "sendWelcome", email);
    }

    private static HttpResponse<String> post(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + path))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        return HTTP.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static JobId jobIdFrom(HttpResponse<String> response) {
        String body = response.body();
        int index = body.lastIndexOf("job id ");
        if (index < 0) {
            throw new AssertionError("No job id in response: " + body);
        }
        return JobId.parse(body.substring(index + "job id ".length()).trim());
    }
}
