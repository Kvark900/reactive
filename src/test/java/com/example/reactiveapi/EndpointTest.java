package com.example.reactiveapi;

import com.example.reactiveapi.domain.Product;
import com.example.reactiveapi.dto.ProductView;
import lombok.*;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EndpointTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    WebClient webClient;

    private volatile int successesCount;


    @Test
    void tests() {
        Flux<Integer> range1 = Flux
                .range(1, 5)
                .doOnNext(integer -> {
                    if (integer == 3) throw new RuntimeException("Not needed integer");
                });

        Flux<Integer> range2 = Flux.range(6, 10);
        Flux<Integer> range3 = Flux.range(11, 20);

        Mono<List<Integer>> merge = Flux
                .merge(range1, range2)
                .onErrorResume(throwable -> Mono.empty())
                .collectList();

        List<Integer> block = merge.block();

        assertNotNull(block);
        assertFalse(block.isEmpty());
        System.out.println(block);
    }

    @Test
    void test() {
        long start = System.currentTimeMillis();
        Map<Integer, List<Double>> map = new HashMap<>();

        Mono<List<ProductView>> listMono = Flux
                .merge(getObjectFluxExchangeResult1(),
                        getObjectFluxExchangeResult2(),
                        getObjectFluxExchangeResult3(),
                        getObjectFluxExchangeResult4())
                .onErrorResume(throwable -> {
                    System.out.println(throwable.getMessage());
                    return Mono.empty();
                })
                .reduce(new HashMap<>(), transformList())
                .map(integerListHashMap ->
                        integerListHashMap
                                .entrySet()
                                .stream()
                                .map(entry -> new ProductView(entry.getKey(), entry.getValue()))
                                .collect(Collectors.toList())
                );


        List<ProductView> productViews = listMono.block();

        long end = System.currentTimeMillis();
        long duration = end - start;

        assertNotNull(productViews);
        assertFalse(productViews.isEmpty());
        System.out.println("Total size is " + productViews.size());
        System.out.println("Elapsed time is " + duration);
        System.out.println("Number of product views: " + productViews.size());
    }

    private BiFunction<HashMap<Integer, List<Double>>, Product, HashMap<Integer, List<Double>>> transformList() {
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


    private Flux<Product> getObjectFluxExchangeResult1() {
        return getProductFlux("https://simple-scala-api.herokuapp.com/api1");
    }

    private Flux<Product> getObjectFluxExchangeResult2() {
        return getProductFlux("https://simple-scala-api.herokuapp.com/api2");
    }

    private Flux<Product> getObjectFluxExchangeResult3() {
        return getProductFlux("https://simple-scala-api.herokuapp.com/api3");
    }

    private Flux<Product> getObjectFluxExchangeResult4() {
        return getProductFlux("https://simple-scala-api.herokuapp.com/api4");
    }

    private Flux<Product> getProductFlux(String s) {
        return webClient
                .get()
                .uri(s)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToFlux(clientResponse -> clientResponse.bodyToFlux(Product.class))
                .doOnComplete(() -> {
                    successesCount++;
                    System.out.printf("Completed request %s at %s\n", s, LocalDateTime.now());
                })
                .doOnNext((product -> {
                    if (successesCount == 3) throw new RuntimeException("Not needed request: " + s);
                }))
                ;
    }

    @Getter
    @Setter
    class ApiSubscriber extends BaseSubscriber {
        private Subscription subscription;

        @Override
        protected void hookOnSubscribe(Subscription subscription) {
            super.hookOnSubscribe(subscription);
            whenNotNeededAbort(subscription);
            this.subscription = subscription;
        }

        @Override
        protected void hookOnNext(Object value) {
            requestUnbounded();
            whenNotNeededAbort(subscription);
        }

        @Override
        protected void hookOnComplete() {
            super.hookOnComplete();
            successesCount++;
        }

        private void whenNotNeededAbort(Subscription subscription) {
            if (successesCount == 3) {
                subscription.cancel();
                throw new RuntimeException("Not needed request");
            }
        }
    }
}
