import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.jobrunr.configuration.JobRunr;
import org.jobrunr.example.services.EmailService;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.cron.Cron;
import org.jobrunr.server.BackgroundJobServerConfiguration;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.utils.StringUtils;

void main() throws Exception {
    var emailService = new EmailService();

    JobRunr.configure() // Using JobRunr Pro? Replace `JobRunr` by `JobRunrPro`
            .useStorageProvider(new InMemoryStorageProvider())
            .useBackgroundJobServer(BackgroundJobServerConfiguration.usingStandardBackgroundJobServerConfiguration().andWorkerCount(10))
            .useDashboard()
            .initialize();

    // JobRunr executes this job every Monday
    BackgroundJob.scheduleRecurrently("weekly-digest", Cron.weekly(DayOfWeek.of(1)),
            emailService::sendWeeklyDigest);

    var server = HttpServer.create(new InetSocketAddress(8080), 0);

    server.createContext("/subscribe", exchange -> {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) { respond(exchange, 405, "Method Not Allowed"); return; }
        var email = getEmailFromQueryParams(exchange);
        if (email == null) { respond(exchange, 400, "Missing email parameter"); return; }
        var jobId = BackgroundJob.enqueue(() -> emailService.sendConfirmation(email)); // JobRunr executes this job asap
        respond(exchange, 202, "Confirmation email queued with job id " + jobId);
    });

    server.createContext("/confirm", exchange -> {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) { respond(exchange, 405, "Method Not Allowed"); return; }
        var email = getEmailFromQueryParams(exchange);
        if (email == null) { respond(exchange, 400, "Missing email parameter"); return; }
        var jobId = BackgroundJob.schedule(
                Instant.now().plus(3, ChronoUnit.DAYS),
                () -> emailService.sendWelcome(email) // JobRunr executes this job 3 days from now
        );
        respond(exchange, 202, "Welcome email scheduled with job id " + jobId);
    });

    server.start();
    IO.println("Listening on http://localhost:8080");
}

String getEmailFromQueryParams(HttpExchange ex) {
    var q = ex.getRequestURI().getQuery();
    var value = q == null ? null : StringUtils.substringAfter(q, "email=");
    return StringUtils.isNotNullOrEmpty(value) ? value : null;
}

void respond(HttpExchange ex, int status, String body) throws IOException {
    var bytes = body.getBytes();
    ex.sendResponseHeaders(status, bytes.length);
    ex.getResponseBody().write(bytes);
    ex.close();
}
