package org.springframework.samples.petclinic.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class ApiGatewayApplicationTests {

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context
        // loads successfully. If the context fails to start,
        // the test will fail automatically.
    }
}
