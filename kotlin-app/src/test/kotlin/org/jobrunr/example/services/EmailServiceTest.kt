package org.jobrunr.example.services

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class EmailServiceTest {

    private val emailService = EmailService()

    @Test
    fun sendConfirmation() {
        val output = captureOutput { emailService.sendConfirmation("alice@example.com") }

        assertThat(output).isEqualTo("Confirmation email sent to alice@example.com")
    }

    @Test
    fun sendWelcome() {
        val output = captureOutput { emailService.sendWelcome("bob@example.com") }

        assertThat(output).isEqualTo("Welcome email sent to bob@example.com")
    }

    @Test
    fun sendWeeklyDigest() {
        val output = captureOutput { emailService.sendWeeklyDigest() }

        assertThat(output).isEqualTo("Weekly digest sent to all subscribers")
    }

    private fun captureOutput(action: () -> Unit): String {
        val originalOut = System.out
        val captured = ByteArrayOutputStream()
        System.setOut(PrintStream(captured))
        try {
            action()
        } finally {
            System.setOut(originalOut)
        }
        return captured.toString().trim()
    }
}
