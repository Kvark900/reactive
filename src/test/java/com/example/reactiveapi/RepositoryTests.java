package com.example.reactiveapi;

import com.example.reactiveapi.domain.Request;
import com.example.reactiveapi.dto.RequestStatsView;
import com.example.reactiveapi.repository.RequestRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.List;

@SpringBootTest
public class RepositoryTests {

    @Autowired
    RequestRepository requestRepository;


    @Test
    void testRepo() {
        Mono<Request> save = requestRepository.save(
                new Request()
                        .withRequest_id(100L)
                        .withUrl("https://simple-scala-api.herokuapp.com/api/1")
        );

        Request request = save.block();
        Assertions.assertNotNull(request);
        List<Request> requests = requestRepository.findAll()
                .collectList()
                .block();

        Assertions.assertNotNull(requests);
        Assertions.assertTrue(requests.contains(request));

    }

    @Test
    void testRequestStats() {
        List<RequestStatsView> requestStats = requestRepository.findRequestStats().collectList().block();
        Assertions.assertNotNull(requestStats);
        Assertions.assertFalse(requestStats.isEmpty());
        System.out.println(requestStats);
    }
}
