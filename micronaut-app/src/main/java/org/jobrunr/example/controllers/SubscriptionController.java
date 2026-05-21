package org.jobrunr.example.controllers;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import org.jobrunr.example.services.EmailService;
import org.jobrunr.scheduling.JobScheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Controller
public class SubscriptionController {

    private final JobScheduler jobScheduler;
    private final EmailService emailService;

    public SubscriptionController(JobScheduler jobScheduler, EmailService emailService) {
        this.jobScheduler = jobScheduler;
        this.emailService = emailService;
    }

    @Post("/subscribe")
    public HttpResponse<String> subscribe(@QueryValue String email) {
        var jobId = jobScheduler.enqueue(() -> emailService.sendConfirmation(email));
        return HttpResponse.accepted().body("Confirmation email queued with job id " + jobId);
    }

    @Post("/confirm")
    public HttpResponse<String> confirm(@QueryValue String email) {
        var jobId = jobScheduler.schedule(Instant.now().plus(3, ChronoUnit.DAYS),
                () -> emailService.sendWelcome(email));
        return HttpResponse.accepted().body("Welcome email scheduled with job id " + jobId);
    }
}
