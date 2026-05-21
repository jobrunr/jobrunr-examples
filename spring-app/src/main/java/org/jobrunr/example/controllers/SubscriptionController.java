package org.jobrunr.example.controllers;

import org.jobrunr.example.services.EmailService;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
public class SubscriptionController {

    private final JobScheduler jobScheduler;
    private final EmailService emailService;

    public SubscriptionController(JobScheduler jobScheduler, EmailService emailService) {
        this.jobScheduler = jobScheduler;
        this.emailService = emailService;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestParam String email) {
        var jobId = jobScheduler.enqueue(() -> emailService.sendConfirmation(email));
        return ResponseEntity.accepted().body("Confirmation email queued with job id " + jobId);
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam String email) {
        var jobId = jobScheduler.schedule(Instant.now().plus(3, ChronoUnit.DAYS),
                () -> emailService.sendWelcome(email));
        return ResponseEntity.accepted().body("Welcome email scheduled with job id " + jobId);
    }
}