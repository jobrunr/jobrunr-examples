package org.jobrunr.example.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jobrunr.example.services.EmailService;
import org.jobrunr.scheduling.JobScheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public class SubscriptionResource {

    @Inject
    JobScheduler jobScheduler;

    @Inject
    EmailService emailService;

    @GET
    @Path("/test")
    public Response loadClass() {
        return Response.accepted("Not important").build();
    }

    @POST
    @Path("/subscribe")
    public Response subscribe(@QueryParam("email") String email) {
        var jobId = jobScheduler.enqueue(() -> emailService.sendConfirmation(email));
        return Response.accepted("Confirmation email queued with job id " + jobId).build();
    }

    @POST
    @Path("/confirm")
    public Response confirm(@QueryParam("email") String email) {
        var jobId = jobScheduler.schedule(Instant.now().plus(3, ChronoUnit.DAYS),
                () -> emailService.sendWelcome(email));
        return Response.accepted("Welcome email scheduled with job id " + jobId).build();
    }
}
