package com.novi.eugene.services;

import com.novi.eugene.data.Image;
import com.novi.eugene.data.ProofOfPlay;
import com.novi.eugene.data.Slideshow;
import com.novi.eugene.data.repo.ImageRepository;
import com.novi.eugene.data.repo.PopRepository;
import com.novi.eugene.data.repo.SlideshowRepository;
import com.novi.eugene.events.Action;
import com.novi.eugene.events.NoviEventPublisher;
import com.novi.eugene.events.SlideshowEvent;
import com.novi.eugene.exceptions.NoviException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Service responsible for recording proof-of-play events,
 * which indicate that a specific image has been shown in a specific slideshow.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PopServiceImpl implements PopService {

    private final SlideshowRepository slideshowRepository;
    private final ImageRepository imageRepository;
    private final PopRepository popRepository;
    private final NoviEventPublisher publisher;

    /**
     * Registers a proof-of-play event by storing it in the database and publishing an event.
     * Ensures that both the slideshow and image exist before proceeding.
     *
     * @param proofOfPlay contains slideshow ID and image ID
     * @return Mono emitting the saved ProofOfPlay entity or an error if references are invalid
     */
    @Override
    public Mono<ProofOfPlay> registerPop(ProofOfPlay proofOfPlay) {
        log.info("Attempting to register proof-of-play: slideshowId={}, imageId={}",
                proofOfPlay.getSlideshowId(), proofOfPlay.getImageId());

        // Load slideshow and image reactively in parallel, or fail with 404 if either doesn't exist
        return Mono.zip(
                slideshowRepository.findById(proofOfPlay.getSlideshowId())
                        .switchIfEmpty(Mono.error(new NoviException("Slideshow " + proofOfPlay.getSlideshowId() + " not found", HttpStatus.NOT_FOUND))),
                imageRepository.findById(proofOfPlay.getImageId())
                        .switchIfEmpty(Mono.error(new NoviException("Image " + proofOfPlay.getImageId() + " not found", HttpStatus.NOT_FOUND)))
        ).flatMap(tuple -> {
            Slideshow slideshow = tuple.getT1();
            Image image = tuple.getT2();

            // Create a new proof-of-play entry
            ProofOfPlay proof = new ProofOfPlay(null, slideshow.getId(), image.getId(), LocalDateTime.now());

            // Save it and emit an event
            return popRepository.save(proof)
                    .doOnSuccess(saved -> {
                        log.info("Proof-of-play registered successfully: {}", saved);
                        publisher.publishEvent(new SlideshowEvent(
                                Action.PLAY,
                                LocalDateTime.now(),
                                slideshow.getId(),
                                slideshow.getName()
                        ));
                    });
        });
    }

    /**
     * PostConstruct hook to verify dependency injection.
     * Only used for debugging during startup.
     */
    @PostConstruct
    public void checkInit() {
        log.debug(">>>>> NoviEventPublisher initialized: {}", publisher);
    }
}
