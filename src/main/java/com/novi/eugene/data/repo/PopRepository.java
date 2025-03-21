package com.novi.eugene.data.repo;

import com.novi.eugene.data.ProofOfPlay;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PopRepository extends ReactiveCrudRepository<ProofOfPlay, Long> {
}
