package com.dev.etl.config;

import com.dev.etl.model.Sport;
import com.dev.etl.repository.SportRepository;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class Setup implements ApplicationRunner {

    private final WebClient.Builder webClientBuilder;
    private final SportRepository sportRepository;

    @Value("${rakuten.application-id}")
    private String applicationId;

    @Value("${rakuten.access-key}")
    private String accessKey;

    @Value("${rakuten.keyword}")
    private String keyword;

    private static final String API_URL =
            "https://openapi.rakuten.co.jp/ichibams/api/IchibaItem/Search/20260701";

    public Setup(WebClient.Builder webClientBuilder, SportRepository sportRepository) {
        this.webClientBuilder = webClientBuilder;
        this.sportRepository = sportRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        setup().block();
    }

    public Mono<Void> setup() {

        WebClient client = webClientBuilder.build();

        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("openapi.rakuten.co.jp")
                        .path("/ichibams/api/IchibaItem/Search/20260701")
                        .queryParam("applicationId", applicationId)
                        .queryParam("keyword", keyword)
                        .queryParam("format", "json")
                        .queryParam("formatVersion", 1)
                        .queryParam("hits", 30)
                        .build())
                .header("accessKey", accessKey)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    System.err.println("Rakuten error: " + body);
                                    return Mono.error(
                                            new RuntimeException(body)
                                    );
                                })
                )
                .bodyToMono(RakutenResponse.class)
                .flatMapMany(response ->
                        Flux.fromIterable(
                                response.getItems() == null
                                        ? List.of()
                                        : response.getItems()
                        )
                )
                .map(item -> new Sport(
                        null,
                        item.getItem().getItemName()
                ))
                .concatMap(sport ->
                        sportRepository.existsByNameIgnoreCase(sport.name())
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.empty();
                                    }

                                    return sportRepository.save(sport)
                                            .onErrorResume(
                                                    DuplicateKeyException.class,
                                                    e -> Mono.empty()
                                            );
                                })
                )
                .then();
    }

    public static class RakutenResponse {

        @JsonProperty("items")
        @JsonAlias("Items")
        private List<RakutenItemWrapper> items;

        public List<RakutenItemWrapper> getItems() {
            return items;
        }

        public void setItems(List<RakutenItemWrapper> items) {
            this.items = items;
        }
    }

    public static class RakutenItemWrapper {

        @JsonProperty("item")
        @JsonAlias("Item")
        private RakutenItem item;

        public RakutenItem getItem() {
            return item;
        }

        public void setItem(RakutenItem item) {
            this.item = item;
        }
    }

    public static class RakutenItem {

        private String itemName;

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }
    }

}