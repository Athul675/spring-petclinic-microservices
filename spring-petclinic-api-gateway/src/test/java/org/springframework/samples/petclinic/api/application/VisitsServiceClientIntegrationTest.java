package org.springframework.samples.petclinic.api.application;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Collections;

class VisitsServiceClientIntegrationTest {

    private MockWebServer server;
    private VisitsServiceClient visitsServiceClient;

    @BeforeEach
    void setup() {
        server = new MockWebServer();
        // We use the builder but don't pass the string since your constructor doesn't take it
        WebClient.Builder builder = WebClient.builder().baseUrl(server.url("/").toString());
        visitsServiceClient = new VisitsServiceClient(builder);
    }

    @AfterEach
    void shutdown() throws IOException {
        server.shutdown();
    }

    @Test
    void getVisitsForPets_shouldReturnVisits() {
        server.enqueue(new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody("{\"items\":[]}"));

        // FIXED: Passing Collections.singletonList(1) because the method expects a List<Integer>
        StepVerifier.create(visitsServiceClient.getVisitsForPets(Collections.singletonList(1)))
            .expectNextMatches(visits -> visits.items().isEmpty())
            .verifyComplete();
    }
}
