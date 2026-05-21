package org.jobrunr.example.services

class EmailService {

    fun sendConfirmation(email: String) {
        println("Confirmation email sent to $email")
    }

    fun sendWelcome(email: String) {
        println("Welcome email sent to $email")
    }

    fun sendWeeklyDigest() {
        println("Weekly digest sent to all subscribers")
    }
}