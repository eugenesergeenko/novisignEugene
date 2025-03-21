package com.novi.eugene.integration.controllers;

import com.novi.eugene.data.Image;
import com.novi.eugene.integration.NoviIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class ImageIntegrationTest extends NoviIntegrationTest {
    @Test
    void testAddImageSuccess() {
        Image image = new Image(null, "http://test.com/img1.jpg", 10, null);

        webTestClient.post()
                .uri("/images/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(image)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Image.class)
                .value(saved -> {
                    assertThat(saved.getId()).isNotNull();
                    assertThat(saved.getUrl()).isEqualTo(image.getUrl());
                });
    }

    @Test
    void testAddImageInvalidUrl() {
        Image image = new Image(null, "invalid.url/img1.jpg", 10, null);

        webTestClient.post()
                .uri("/images/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(image)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid image URL");
    }

    @Test
    public void testDeleteImageOk() {
        Image image = new Image(null, "https://test.com/del.jpg", 10, null);

        Long imageId = webTestClient.post()
                .uri("/images/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(image)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Image.class)
                .returnResult()
                .getResponseBody()
                .getId();

        webTestClient.delete()
                .uri("/images/delete/" + imageId)
                .exchange()
                .expectStatus().isNoContent();

    }

    @Test
    void testDeleteImageNotFound() {
        webTestClient.delete()
                .uri("/images/delete/7171")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Image not found")
                .jsonPath("$.status").isEqualTo(404);
    }

}
