package com.dev.etl.repository;

import com.dev.etl.model.Sport;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SportRepository
        extends ReactiveCrudRepository<Sport, Integer> {

    Mono<Boolean> existsByNameIgnoreCase(String name);

    Flux<Sport> findByNameContainingIgnoreCase(String name);

}