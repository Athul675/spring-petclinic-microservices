package org.springframework.samples.petclinic.api.boundary.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.samples.petclinic.api.application.CustomersServiceClient;
import org.springframework.samples.petclinic.api.application.VisitsServiceClient;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(controllers = ApiGatewayController.class)
class ApiGatewayControllerTest {

    @Autowired
    private WebTestClient client;

    @MockBean
    private CustomersServiceClient customersServiceClient;

    @MockBean
    private VisitsServiceClient visitsServiceClient;

    @Test
    void getOwnerDetails_shouldReturnOk() {
        client.get()
            .uri("/api/gateway/owners/1")
            .exchange()
            .expectStatus().isOk();
    }
}
