package com.novi.eugene.data.repo;

import com.novi.eugene.data.Slideshow;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlideshowRepository extends ReactiveCrudRepository<Slideshow, Long> {
}
