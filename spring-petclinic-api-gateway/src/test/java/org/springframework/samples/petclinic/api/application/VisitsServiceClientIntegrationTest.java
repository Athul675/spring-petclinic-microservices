package org.springframework.samples.petclinic.api.application;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

class VisitsServiceClientIntegrationTest {

    private MockWebServer server;
    private VisitsServiceClient visitsServiceClient;

    @BeforeEach
    void setup() {
        server = new MockWebServer();
        String baseUrl = server.url("/").toString();
        visitsServiceClient = new VisitsServiceClient(WebClient.builder(), baseUrl);
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

        StepVerifier.create(visitsServiceClient.getVisitsForPets(1))
            .expectNextMatches(visits -> visits.items().isEmpty())
            .verifyComplete();
    }
}
