package com.novi.eugene.integration.services;

import com.novi.eugene.data.Image;
import com.novi.eugene.dto.SlideshowRequest;
import com.novi.eugene.integration.NoviIntegrationTest;
import com.novi.eugene.services.ImageService;
import com.novi.eugene.services.SlideshowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;


public class SlideshowSericeTest extends NoviIntegrationTest {

    @Autowired
    private SlideshowService slideshowService;

    @Autowired
    private ImageService imageService;

    @Test
    void addSlideshowWithValidImages_shouldSucceed() {
        // Step 1: Add an image first
        Image image = new Image(null, "http://test.com/img1.jpg", 10, null);

        Mono<Long> savedImageId = imageService.addImage(image).map(Image::getId);

        // Step 2: Use image ID to build slideshow request
        StepVerifier.create(savedImageId.flatMap(imageId -> {
                    SlideshowRequest request = new SlideshowRequest(
                            "Test Slideshow",
                            List.of(imageId)
                    );
                    return slideshowService.addSlideshow(request);
                }))
                .expectNextMatches(slideshow ->
                        slideshow.getId() != null &&
                                slideshow.getName().equals("Test Slideshow")
                )
                .verifyComplete();
    }
}
