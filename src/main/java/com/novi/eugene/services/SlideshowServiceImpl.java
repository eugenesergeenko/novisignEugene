package com.novi.eugene.services;

import com.novi.eugene.data.Image;
import com.novi.eugene.data.Slideshow;
import com.novi.eugene.data.SlideshowImage;
import com.novi.eugene.data.repo.ImageRepository;
import com.novi.eugene.data.repo.SlideshowImageRepository;
import com.novi.eugene.data.repo.SlideshowRepository;
import com.novi.eugene.dto.SlideshowRequest;
import com.novi.eugene.events.Action;
import com.novi.eugene.events.NoviEventPublisher;
import com.novi.eugene.events.SlideshowEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Service implementation for handling slideshow-related operations such as
 * adding a new slideshow, deleting it, and retrieving its images.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SlideshowServiceImpl implements SlideshowService {

    private final SlideshowRepository slideshowRepository;
    private final ImageRepository imageRepository;
    private final SlideshowImageRepository slideshowImageRepository;
    private final NoviEventPublisher publisher;

    /**
     * Creates a new slideshow and associates it with a list of images and durations.
     * Emits a CREATE event upon success.
     *
     * @param slideshowRequest the slideshow name and associated image details
     * @return Mono emitting the saved slideshow
     */
    @Override
    public Mono<Slideshow> addSlideshow(SlideshowRequest slideshowRequest) {
        Slideshow slideshow = Slideshow.builder().name(slideshowRequest.getName()).build();
        log.info("Creating slideshow: {}", slideshowRequest.getName());

        // Save slideshow first, then associate images
        return slideshowRepository.save(slideshow).flatMap(savedSShow -> {
            Flux<SlideshowImage> sShowImages = Flux.fromIterable(slideshowRequest.getImages())
                    .flatMap(imgId -> slideshowImageRepository.save(new SlideshowImage(null, savedSShow.getId(), imgId)));
            return sShowImages.then(Mono.just(savedSShow))
                    .doOnSuccess(unused ->
                            publisher.publishEvent(new SlideshowEvent(Action.CREATE, savedSShow.getAddedAt(), savedSShow.getId(), savedSShow.getName()))
                    );
        });
    }

    @Override
    /**
     * Deletes a slideshow by ID and publishes a DELETE event.
     *
     * @param id the ID of the slideshow to delete
     * @return Mono signaling completion or error if not found
     */
    public Mono<Void> deleteSlideshow(Long id) {
        log.info("Attempting to delete slideshow with ID: {}", id);

        return slideshowRepository.findById(id)
                .flatMap(slideshow ->
                        slideshowRepository.deleteById(id)
                                .doOnSuccess(unused -> {
                                    log.info("Slideshow '{}' (ID: {}) deleted", slideshow.getName(), id);
                                    publisher.publishEvent(new SlideshowEvent(
                                            Action.DELETE,
                                            LocalDateTime.now(),
                                            id,
                                            slideshow.getName()
                                    ));
                                })
                );
    }

    /**
     * Retrieves all images associated with the given slideshow ID,
     * ordered by their internal image ID.
     *
     * @param slideshowId the ID of the slideshow
     * @return Flux emitting the images in slideshow order
     */
    @Override
    public Flux<Image> getImages(Long slideshowId) {
        log.info("Fetching images for slideshow ID: {}", slideshowId);

        return slideshowImageRepository.findBySlideshowIdOrderByImageIdAsc(slideshowId)
                .flatMap(slideshowImage -> {
                    log.debug("Fetching image ID: {} for slideshow ID: {}", slideshowImage.getImageId(), slideshowId);
                    return imageRepository.findById(slideshowImage.getImageId());
                });
    }
}
