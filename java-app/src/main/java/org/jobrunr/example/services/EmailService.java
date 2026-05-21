package org.jobrunr.example.services;

public class EmailService {

    public void sendConfirmation(String email) {
        IO.println("Confirmation email sent to " + email);
    }

    public void sendWelcome(String email) {
        IO.println("Welcome email sent to " + email);
    }

    public void sendWeeklyDigest() {
        IO.println("Weekly digest sent to all subscribers");
    }
}

