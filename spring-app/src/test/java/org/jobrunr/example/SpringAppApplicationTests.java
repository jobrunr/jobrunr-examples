package org.jobrunr.example;

import org.jobrunr.example.services.EmailService;
import org.jobrunr.jobs.Job;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.RecurringJob;
import org.jobrunr.storage.StorageProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jobrunr.jobs.JobAssert.assertThat;
import static org.jobrunr.jobs.RecurringJobAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "jobrunr.background-job-server.enabled=false",
        "jobrunr.dashboard.enabled=false"
})
@AutoConfigureMockMvc
class SpringAppApplicationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    StorageProvider storageProvider;

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
    void subscribeEndpointEnqueuesConfirmationEmail() throws Exception {
        String email = "alice@example.com";

        MvcResult result = mockMvc.perform(post("/subscribe").param("email", email))
                .andExpect(status().isAccepted())
                .andReturn();

        Job job = storageProvider.getJobById(jobIdFrom(result.getResponse().getContentAsString()));
        assertThat(job)
                .hasJobDetails(EmailService.class, "sendConfirmation", email);
    }

    @Test
    void confirmEndpointSchedulesWelcomeEmail() throws Exception {
        String email = "bob@example.com";

        MvcResult result = mockMvc.perform(post("/confirm").param("email", email))
                .andExpect(status().isAccepted())
                .andReturn();

        Job job = storageProvider.getJobById(jobIdFrom(result.getResponse().getContentAsString()));
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
