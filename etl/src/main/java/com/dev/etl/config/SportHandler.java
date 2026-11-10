package com.dev.etl.config;

import com.dev.etl.model.Sport;
import com.dev.etl.repository.SportRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class SportHandler {

    private final SportRepository sportRepository;

    public SportHandler(SportRepository sportRepository) {
        this.sportRepository = sportRepository;
    }

    public Mono<ServerResponse> createSport(ServerRequest request) {

        String sportName = request.pathVariable("sportname").trim();

        if (sportName.isBlank()) {
            return ServerResponse.badRequest()
                    .bodyValue("Sport name cannot be empty");
        }

        return sportRepository.existsByNameIgnoreCase(sportName)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(
                                new DuplicateKeyException(
                                        "Sport already exists: " + sportName
                                )
                        );
                    }
                    Sport sport = new Sport(null, sportName);
                    return sportRepository.save(sport);
                })
                .flatMap(saved -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(saved))
                .onErrorResume(DuplicateKeyException.class, exception ->
                        ServerResponse.status(HttpStatus.CONFLICT)
                                .bodyValue("Sport already exists: " + sportName)
                );
    }

    public Mono<ServerResponse> searchSports(ServerRequest request) {

        String query = request.queryParam("q").orElse("").trim();

        return sportRepository.findByNameContainingIgnoreCase(query)
                .collectList()
                .flatMap(sports -> ServerResponse.ok().bodyValue(sports));
    }

}