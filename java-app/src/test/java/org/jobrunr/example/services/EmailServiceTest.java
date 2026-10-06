package org.jobrunr.example.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

class EmailServiceTest {

    private final EmailService emailService = new EmailService();

    @Test
    void sendConfirmation() {
        String output = captureOutput(() -> emailService.sendConfirmation("alice@example.com"));

        assertThat(output).isEqualTo("Confirmation email sent to alice@example.com");
    }

    @Test
    void sendWelcome() {
        String output = captureOutput(() -> emailService.sendWelcome("bob@example.com"));

        assertThat(output).isEqualTo("Welcome email sent to bob@example.com");
    }

    @Test
    void sendWeeklyDigest() {
        String output = captureOutput(emailService::sendWeeklyDigest);

        assertThat(output).isEqualTo("Weekly digest sent to all subscribers");
    }

    private static String captureOutput(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return captured.toString().strip();
    }

}
