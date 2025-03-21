package com.novi.eugene.integration;

import com.novi.eugene.data.Image;
import com.novi.eugene.events.NoviEventPublisher;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")  //Uses application-test.yml with H2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class NoviIntegrationTest {

    @Autowired
    protected WebTestClient webTestClient;

    @MockBean
    protected NoviEventPublisher publisher;

    @BeforeEach
    void setUp() {
        System.out.println("Starting Integration Test...");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Integration Test Completed.");
    }

    protected Long createImage(String url) {
        Image image = new Image(null, url, 10, null);

        return webTestClient.post()
                .uri("/images/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(image)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Image.class)
                .returnResult()
                .getResponseBody()
                .getId();
    }

}
