package com.novi.eugene.controllers;

import com.novi.eugene.Application;
import com.novi.eugene.data.Image;
import com.novi.eugene.exceptions.NoviException;
import com.novi.eugene.services.ImageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@WebFluxTest(ImageController.class)
public class ImageControllerTest extends NoviControllerTest {

    @MockBean
    private ImageService imageService;

    /**
     * Test: Add image
     * - HTTP 200 Ok
     * - Image properties transferred within the body
     */
    @Test
    public void testAddImage() {
        Image image = new Image(71L, "test.com/image.jpg", 10, null);
        when(imageService.addImage(any(Image.class))).thenReturn(Mono.just(image));

        webTestClient.post().uri("/images/add")
                .bodyValue(image)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Image.class).value(savedImg -> {
                    assert savedImg.getId().equals(71L);
                    assert savedImg.getUrl().equals("test.com/image.jpg");
                });
        verify(imageService, times(1)).addImage(any(Image.class));
    }

    /**
     * Test: Delete Image (Ok)
     * - HTTP 204 No Content
     */
    @Test
    public void testDeleteImageOk() {
        when(imageService.deleteImage(anyLong())).thenReturn(Mono.empty());

        webTestClient.delete().uri("/images/delete/71").exchange().expectStatus().isNoContent();

        verify(imageService, times(1)).deleteImage(71L);
    }

    /**
     * Test Delete Image (Not Found)
     */
    @Test
    public void testDeleteImageNotFound() {
        when(imageService.deleteImage(anyLong())).thenReturn(Mono.error(new NoviException("Image not found", HttpStatus.NOT_FOUND)));

        webTestClient.delete()
                .uri("/images/delete/71")
                .exchange()
                .expectStatus().isNotFound().expectBody().jsonPath("$.message").isEqualTo("Image not found");

        verify(imageService, times(1)).deleteImage(71L);
    }

    /**
     * Test: Find Images (OK)
     * - HTTP 200 OK
     */
    @Test
    public void testFindImagesOk() {
        Image img1 = new Image(71L, "test.com/image1.jpg", 10, LocalDateTime.now());
        Image img2 = new Image(71L, "test.com/image2.jpg", 10, img1.getAddedAt());
        when(imageService.findImagesByUrl("test")).thenReturn(Flux.just(img1, img2));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/images/search").queryParam("urlFragment", "test").build())
                .exchange().expectStatus().isOk().expectBodyList(Image.class).hasSize(2).contains(img1, img2);

        verify(imageService, times(1)).findImagesByUrl("test");
    }
}