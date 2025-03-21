package com.novi.eugene.controllers;

import com.novi.eugene.data.Image;
import com.novi.eugene.data.Slideshow;
import com.novi.eugene.dto.SlideshowRequest;
import com.novi.eugene.exceptions.NoviException;
import com.novi.eugene.services.PopService;
import com.novi.eugene.services.SlideshowService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@WebFluxTest(SlideshowController.class)
public class SlideshowControllerTest extends NoviControllerTest {

    @MockBean
    private SlideshowService slideshowService;

    @MockBean
    private PopService popService;

    @Test
    public void testAddSlideshowOk() {

        SlideshowRequest request = new SlideshowRequest("Test Slideshow", List.of(71L, 72L));
        Slideshow savedSShow = new Slideshow(1L, request.getName(), LocalDateTime.now());
        when(slideshowService.addSlideshow(any(SlideshowRequest.class))).thenReturn(Mono.just(savedSShow));

        webTestClient.post()
                .uri("/slideshow/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Slideshow.class)
                .value(savedSlideshow -> {
                    assert savedSlideshow.getId().equals(1L);
                    assert savedSlideshow.getName().equals("Test Slideshow");
                });

        verify(slideshowService, times(1)).addSlideshow(any(SlideshowRequest.class));
    }

    @Test
    public void testAddSlideshowMissingName() {
        SlideshowRequest request = new SlideshowRequest(null, List.of());

        when(slideshowService.addSlideshow(any(SlideshowRequest.class)))
                .thenReturn(Mono.error(new NoviException("Slideshow name is required", HttpStatus.BAD_REQUEST)));

        webTestClient.post()
                .uri("/slideshow/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Slideshow name is required");

        verify(slideshowService, times(1)).addSlideshow(any(SlideshowRequest.class));
    }

    @Test
    public void testDeleteSlideshowOk() {
        when(slideshowService.deleteSlideshow(anyLong())).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/slideshow/delete/1")
                .exchange()
                .expectStatus().isNoContent();

        verify(slideshowService, times(1)).deleteSlideshow(1L);
    }

    @Test
    public void testDeleteSlideshowNotFound() {
        when(slideshowService.deleteSlideshow(anyLong()))
                .thenReturn(Mono.error(new NoviException("Slideshow not found", HttpStatus.NOT_FOUND)));

        webTestClient.delete()
                .uri("/slideshow/delete/101")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Slideshow not found");

        verify(slideshowService, times(1)).deleteSlideshow(101L);
    }

    @Test
    public void testGetSlideshowImagesOk() {
        List<Image> images = List.of(
                new Image(71L, "test.com/image1.jpg", 10, LocalDateTime.now()),
                new Image(72L, "test.com/image2.jpg", 10, LocalDateTime.now())
        );

        when(slideshowService.getImages(1L))
                .thenReturn(Flux.fromIterable(images));

        webTestClient.get()
                .uri("/slideshow/1/images")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Image.class)
                .hasSize(2)
                .contains(images.get(0), images.get(1));

        verify(slideshowService, times(1)).getImages(1L);
    }

    @Test
    public void testGetSlideshowImagesNotFound() {
        when(slideshowService.getImages(anyLong()))
                .thenReturn(Flux.error(new NoviException("Slideshow not found", HttpStatus.NOT_FOUND)));

        webTestClient.get()
                .uri("/slideshow/101/images")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Slideshow not found");

        verify(slideshowService, times(1)).getImages(101L);
    }

}
