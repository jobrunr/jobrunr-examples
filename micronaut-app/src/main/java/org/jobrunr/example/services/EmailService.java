package org.jobrunr.example.services;

import jakarta.inject.Singleton;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;

@Singleton
public class EmailService {

    @Recurring(id = "weekly-digest", cron = "0 9 * * MON")
    @Job(name = "Weekly Digest")
    public void sendWeeklyDigest() {
        System.out.println("Weekly digest sent to all subscribers");
    }

    public void sendConfirmation(String email) {
        System.out.println("Confirmation email sent to " + email);
    }

    public void sendWelcome(String email) {
        System.out.println("Welcome email sent to " + email);
    }
}
