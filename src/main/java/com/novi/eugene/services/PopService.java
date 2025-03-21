package com.novi.eugene.services;

import com.novi.eugene.data.ProofOfPlay;
import reactor.core.publisher.Mono;

public interface PopService {
    Mono<ProofOfPlay> registerPop(ProofOfPlay proofOfPlay);
}
