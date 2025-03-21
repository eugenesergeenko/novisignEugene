package com.novi.eugene.services;

import com.novi.eugene.data.Image;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ImageService {

    Mono<Image> addImage(Image image);
    Mono<Void> deleteImage(Long id);
    Flux<Image> findImagesByUrl(String urlFragment);
}
