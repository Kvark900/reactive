package com.example.reactiveapi.service;

import com.example.reactiveapi.domain.Product;
import com.example.reactiveapi.domain.Request;
import com.example.reactiveapi.dto.ProductView;
import com.example.reactiveapi.dto.RequestStatsView;
import com.example.reactiveapi.repository.ProductRepository;
import com.example.reactiveapi.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductService {

    private static final String BASE_PRODUCT_URL = "https://simple-scala-api.herokuapp.com/api";
    private final WebClient webClient;
    private volatile int successesCount;

    private final RequestRepository requestRepository;


    public Mono<List<ProductView>> getProducts() {
        successesCount = 0;
        long start = System.currentTimeMillis();
        return Flux.merge(getProductsOne(),
                        getProductsTwo(),
                        getProductsThree(),
                        getProductsFour())
                .onErrorResume(throwable -> {
                    log.error(throwable.getMessage());
                    return Mono.empty();
                })
                .doOnComplete(() -> {
                    long end = System.currentTimeMillis();
                    log.info("Merging all request took: {}", end - start);
                })
                .reduce(new HashMap<>(), reduceProductListToMap())
                .map(this::convertToProductViewList);
    }

    private List<ProductView> convertToProductViewList(HashMap<Integer, List<Double>> hashMap) {
        return hashMap.entrySet()
                .stream()
                .map(entry -> new ProductView(entry.getKey(), entry.getValue()))
                .collect(toList());
    }

    private BiFunction<HashMap<Integer, List<Double>>, Product, HashMap<Integer, List<Double>>>
    reduceProductListToMap() {
        return (m, product) -> {
            int id = product.getProduct_id();
            if (!m.containsKey(id)) {
                List<Double> prices = new ArrayList<>();
                prices.add(product.getPrice());
                m.put(id, prices);
            } else m.get(id).add(product.getPrice());
            return m;
        };
    }

    private Flux<Product> getProductsOne() {
        return getProductFlux(BASE_PRODUCT_URL + "1");
    }

    private Flux<Product> getProductsTwo() {
        return getProductFlux(BASE_PRODUCT_URL + "2");
    }

    private Flux<Product> getProductsThree() {
        return getProductFlux(BASE_PRODUCT_URL + "3");
    }

    private Flux<Product> getProductsFour() {
        return getProductFlux(BASE_PRODUCT_URL + "4");
    }

    private Flux<Product> getProductFlux(String uri) {
        long start = System.currentTimeMillis();
        return webClient
                .get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToFlux(clientResponse -> clientResponse.bodyToFlux(Product.class))
                .doOnNext((product -> {
                    if (successesCount == 3) throw new RuntimeException("Not needed request: " + uri);
                }))
                .doOnComplete(() -> {
                    long end = System.currentTimeMillis();
                    long duration = end - start;
                    log.info("Time elapsed for request {} is {}", uri, duration);
                    log.info("Completed request {} at {}\n", uri, LocalDateTime.now());
                    successesCount++;
                    saveRequest(Request
                            .builder()
                            .url(uri)
                            .response_time(duration)
                            .request_executed_at(LocalDateTime.now())
                            .build()).subscribe();
                })
                ;
    }

    @Transactional
    public Mono<Request> saveRequest(Request request) {
        return requestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public Flux<RequestStatsView> getRequestStatsForCurrentDate() {
        return requestRepository.findRequestStats();
    }
}
