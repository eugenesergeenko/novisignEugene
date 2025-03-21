package com.novi.eugene.services;

import com.novi.eugene.data.Image;
import com.novi.eugene.data.repo.ImageRepository;
import com.novi.eugene.events.Action;
import com.novi.eugene.events.ImageEvent;
import com.novi.eugene.events.NoviEventPublisher;
import com.novi.eugene.exceptions.NoviException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * Service implementation for managing Image data and business logic.
 * Handles creation, deletion, and search of images.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final NoviEventPublisher publisher;

    // Pattern to validate supported image URL extensions
    private static final Pattern IMAGE_URL_PATTERN = Pattern.compile(
            "^(https?|ftp):\\/\\/.*\\.(?:png|jpg|jpeg|gif|bmp|webp)$", Pattern.CASE_INSENSITIVE
    );

    /**
     * Adds a new image after validating the URL.
     * Publishes an event upon successful addition.
     *
     * @param image The image to be added
     * @return Mono emitting the saved image or an error if validation fails
     */
    @Override
    public Mono<Image> addImage(Image image) {
        if (!validateImageUrl(image.getUrl())) {
            log.error("Invalid image URL: {}", image.getUrl());
            return Mono.error(new NoviException("Invalid image URL", HttpStatus.BAD_REQUEST));
        }

        return imageRepository.save(image)
                .doOnSuccess(savedImage -> {
                    log.info("Image added successfully: {}", savedImage);
                    publisher.publishEvent(new ImageEvent(
                            Action.ADD,
                            LocalDateTime.now(),
                            savedImage.getId(),
                            savedImage.getUrl()
                    ));
                })
                // Return a fresh instance (if needed for downstream formatting)
                .map(savedImage -> new Image(
                        savedImage.getId(),
                        savedImage.getUrl(),
                        savedImage.getDuration(),
                        savedImage.getAddedAt()
                ));
    }

    /**
     * Deletes an image by ID. Emits an event if deletion is successful.
     *
     * @param id The ID of the image to delete
     * @return Mono signaling completion or error if image not found
     */
    @Override
    public Mono<Void> deleteImage(Long id) {
        return imageRepository.findById(id)
                .switchIfEmpty(Mono.error(new NoviException("Image not found", HttpStatus.NOT_FOUND)))
                .flatMap(image -> {
                    log.info("Deleting image: {}", image);
                    return imageRepository.delete(image)
                            .doOnSuccess(unused -> {
                                log.info("Image deleted successfully: {}", image);
                                publisher.publishEvent(new ImageEvent(
                                        Action.DELETE,
                                        LocalDateTime.now(),
                                        image.getId(),
                                        image.getUrl()
                                ));
                            });
                });
    }

    /**
     * Searches for images whose URL contains a specified fragment.
     *
     * @param urlFragment Partial string to match against image URLs
     * @return Flux of matching images
     */
    @Override
    public Flux<Image> findImagesByUrl(String urlFragment) {
        log.info("Searching for images with URL fragment: {}", urlFragment);
        return imageRepository.findByUrlLike(urlFragment);
    }

    /**
     * Validates whether the image URL is in a proper format and supported extension.
     *
     * @param imageUrl URL to validate
     * @return true if valid, false otherwise
     */
    private boolean validateImageUrl(String imageUrl) {
        boolean isValid = IMAGE_URL_PATTERN.matcher(imageUrl).matches();
        log.info("Image URL Validation: {} - Valid: {}", imageUrl, isValid);
        return isValid;
    }
}
