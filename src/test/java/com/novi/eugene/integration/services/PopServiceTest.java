package com.novi.eugene.integration.services;

import com.novi.eugene.data.Image;
import com.novi.eugene.data.ProofOfPlay;
import com.novi.eugene.dto.SlideshowRequest;
import com.novi.eugene.integration.NoviIntegrationTest;
import com.novi.eugene.services.ImageService;
import com.novi.eugene.services.PopService;
import com.novi.eugene.services.SlideshowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

public class PopServiceTest extends NoviIntegrationTest {
    @Autowired
    private PopService proofOfPlayService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private SlideshowService slideshowService;

    @Test
    void registerPop_shouldSucceed() {
        Image image = new Image(null, "http://test.com/img1.jpg", 10, null);

        Mono<ProofOfPlay> result = imageService.addImage(image)
                .flatMap(savedImage -> {
                    SlideshowRequest request = new SlideshowRequest("Pop Test",
                            List.of(savedImage.getId()));

                    return slideshowService.addSlideshow(request)
                            .flatMap(slideshow ->
                                    proofOfPlayService.registerPop(ProofOfPlay.builder().slideshowId(slideshow.getId()).imageId(image.getId()).build())
                            );
                });

        StepVerifier.create(result)
                .expectNextMatches(pop ->
                        pop.getId() != null &&
                                pop.getImageId() != null &&
                                pop.getSlideshowId() != null &&
                                pop.getPlayedAt() != null
                )
                .verifyComplete();
    }

    @Test
    void registerPop_shouldFailWhenImageNotFound() {
        Mono<ProofOfPlay> result = slideshowService.addSlideshow(new SlideshowRequest("With Bad Image", List.of()))
                .flatMap(slideshow -> proofOfPlayService.registerPop(ProofOfPlay.builder().slideshowId(slideshow.getId()).imageId(7171L).build())); // fake image ID

        StepVerifier.create(result)
                .expectErrorMatches(e ->
                        e.getMessage().contains("Image 7171 not found")
                )
                .verify();
    }

    @Test
    void registerPop_shouldFailWhenSlideshowNotFound() {
        Image image = new Image(null, "http://test.com/pop.jpg", 5, null);

        Mono<ProofOfPlay> result = imageService.addImage(image)
                .flatMap(savedImage -> proofOfPlayService.registerPop(ProofOfPlay.builder().slideshowId(7171L).imageId(image.getId()).build())); // fake slideshow ID

        StepVerifier.create(result)
                .expectErrorMatches(e ->
                        e.getMessage().contains("Slideshow 7171 not found")
                )
                .verify();
    }

}
