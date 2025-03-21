package com.novi.eugene.data.repo;

import com.novi.eugene.data.SlideshowImage;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface SlideshowImageRepository extends ReactiveCrudRepository<SlideshowImage, Long> {
    Flux<SlideshowImage> findBySlideshowIdOrderByImageIdAsc(Long slideshowId);
}
