package com.novi.eugene.data.repo;

import com.novi.eugene.data.Image;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ImageRepository extends ReactiveCrudRepository<Image, Long> {
    @Query("SELECT * FROM image WHERE url LIKE CONCAT('%', :fragment, '%')")
    Flux<Image> findByUrlLike(String fragment);
}
