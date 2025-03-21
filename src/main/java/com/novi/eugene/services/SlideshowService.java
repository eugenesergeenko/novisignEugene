package com.novi.eugene.services;

import com.novi.eugene.data.Image;
import com.novi.eugene.data.Slideshow;
import com.novi.eugene.dto.SlideshowRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SlideshowService {
    Mono<Slideshow> addSlideshow(SlideshowRequest slideshowRequest);
    Mono<Void> deleteSlideshow(Long id);
    Flux<Image> getImages(Long slideshowId);
}
