package org.jobrunr.example.resources;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import org.jobrunr.example.services.EmailService;
import org.jobrunr.jobs.Job;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.RecurringJob;
import org.jobrunr.storage.StorageProvider;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.jobrunr.jobs.JobAssert.assertThat;
import static org.jobrunr.jobs.RecurringJobAssert.assertThat;

@QuarkusTest
@TestProfile(SubscriptionResourceTest.SubscriptionResourceTestProfile.class)
class SubscriptionResourceTest {

    @Inject
    StorageProvider storageProvider;

    public static class SubscriptionResourceTestProfile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "quarkus.jobrunr.background-job-server.enabled", "false",
                    "quarkus.jobrunr.dashboard.enabled", "false"
            );
        }
    }

    @Test
    void recurringJobIsCreatedAfterStartup() {
        RecurringJob recurringJob = storageProvider.getRecurringJobs()
                .stream()
                .filter(rj -> "weekly-digest".equals(rj.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(recurringJob)
                .hasId("weekly-digest")
                .hasScheduleExpression("0 9 * * MON")
                .hasJobDetails(EmailService.class, "sendWeeklyDigest");
    }

    @Test
    void subscribeEndpointEnqueuesConfirmationEmail() {
        String email = "alice@example.com";

        String body = RestAssured.given()
                .queryParam("email", email)
                .when().post("/subscribe")
                .then()
                .statusCode(202)
                .extract().body().asString();

        Job job = storageProvider.getJobById(jobIdFrom(body));
        assertThat(job)
                .hasJobDetails(EmailService.class, "sendConfirmation", email);
    }

    @Test
    void confirmEndpointSchedulesWelcomeEmail() {
        String email = "bob@example.com";

        String body = RestAssured.given()
                .queryParam("email", email)
                .when().post("/confirm")
                .then()
                .statusCode(202)
                .extract().body().asString();

        Job job = storageProvider.getJobById(jobIdFrom(body));
        assertThat(job)
                .hasJobDetails(EmailService.class, "sendWelcome", email);
    }

    private static JobId jobIdFrom(String body) {
        int index = body.lastIndexOf("job id ");
        if (index < 0) {
            throw new AssertionError("No job id in response: " + body);
        }
        return JobId.parse(body.substring(index + "job id ".length()).trim());
    }
}
