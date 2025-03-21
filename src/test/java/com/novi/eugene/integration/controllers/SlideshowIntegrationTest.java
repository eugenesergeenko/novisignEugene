package com.novi.eugene.integration.controllers;

import com.novi.eugene.data.ProofOfPlay;
import com.novi.eugene.data.Slideshow;
import com.novi.eugene.dto.SlideshowRequest;
import com.novi.eugene.integration.NoviIntegrationTest;
import com.novi.eugene.services.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
public class SlideshowIntegrationTest extends NoviIntegrationTest {

    @Autowired
    private ImageService imageService;

    @Test
    public void testAddSlideshowOk() {

        Long img1 =  createImage("http://test.com/image1.jpg");
        Long img2 = createImage("http://test.com/image2.jpg");


        SlideshowRequest request = new SlideshowRequest("Test SShow1", List.of(img1, img2));

        Slideshow resp = webTestClient.post()
                .uri("/slideshow/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Slideshow.class)
                .returnResult()
                .getResponseBody();

        assertThat(resp).isNotNull();
        assertThat(resp.getId()).isNotNull();
        assertThat(resp.getName()).isEqualTo(request.getName());
    }

    @Test
    void testAddAndDeleteSlideshow() {
        // Step 1: Create test images
        Long img1 =  createImage("http://test.com/image1.jpg");
        Long img2 = createImage("http://test.com/image2.jpg");

        // Step 2: Create slideshow request
        SlideshowRequest request = new SlideshowRequest(
                "Test SShow2",
                List.of(img1, img2)
        );

        // Step 3: Send POST /slideShow/addSlideshow
        Slideshow slideshow = webTestClient.post()
                .uri("/slideshow/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Slideshow.class)
                .returnResult()
                .getResponseBody();

        // Step 4: Validate response
        assertThat(slideshow).isNotNull();
        assertThat(slideshow.getId()).isNotNull();
        assertThat(slideshow.getName()).isEqualTo("Test SShow2");

        // Step 5: Delete slideshow
        webTestClient.delete()
                .uri("/slideshow/delete/" + slideshow.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testRegisterPopOk() {
        // Step 1: Create an image
        Long imageId = createImage("http://test.com/play.jpg");

        // Step 2: Create a slideshow with that image
        SlideshowRequest request = new SlideshowRequest(
                "Test Slideshow",
                List.of(imageId)
        );

        Slideshow slideshow = webTestClient.post()
                .uri("/slideshow/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Slideshow.class)
                .returnResult()
                .getResponseBody();

        // Step 3: Record a proof-of-play
        ProofOfPlay proof = webTestClient.post()
                .uri("/slideshow/" + slideshow.getId() + "/proof-of-play/" + imageId)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProofOfPlay.class)
                .returnResult()
                .getResponseBody();

        // Step 4: Assert proof-of-play was recorded
        assertThat(proof).isNotNull();
        assertThat(proof.getSlideshowId()).isEqualTo(slideshow.getId());
        assertThat(proof.getImageId()).isEqualTo(imageId);
        assertThat(proof.getPlayedAt()).isNotNull();
    }

    @Test
    void testRegisterPopNonexistentImage() {
        Long fakeImageId = 99999L;
        Long fakeSlideshowId = 88888L;

        webTestClient.post()
                .uri("/slideshow/" + fakeSlideshowId + "/proof-of-play/" + fakeImageId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").value(msg ->
                        assertThat(((String) msg).toLowerCase()).contains("not found"))
                .jsonPath("$.status").isEqualTo(404);
    }
}
