package com.novi.eugene.integration.services;

import com.novi.eugene.data.Image;
import com.novi.eugene.integration.NoviIntegrationTest;
import com.novi.eugene.services.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;

class ImageServiceTest extends NoviIntegrationTest {
    @Autowired
    private ImageService imageService;

    @Test
    void addValidImage_shouldPersistAndReturnImage() {
        Image image = new Image(null, "http://test.com/img1.jpg", 5, null);

        StepVerifier.create(imageService.addImage(image))
                .expectNextMatches(img ->
                        img.getId() != null &&
                                img.getUrl().equals("http://test.com/img1.jpg") &&
                                img.getDuration() == 5
                )
                .verifyComplete();
    }

    @Test
    void addInvalidImage_shouldReturnError() {
        Image image = new Image(null, "img1.txt", 5, null);

        StepVerifier.create(imageService.addImage(image))
                .expectErrorMatches(e ->
                        e.getMessage().equals("Invalid image URL")
                )
                .verify();
    }
}