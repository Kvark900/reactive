package com.example.reactiveapi;

import com.example.reactiveapi.domain.Request;
import com.example.reactiveapi.dto.RequestStatsView;
import com.example.reactiveapi.repository.RequestRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Log4j2
public class RepositoryTests {

    @Autowired
    RequestRepository requestRepository;


    @Test
    void testRepo() {
        Mono<Request> save = requestRepository.save(
                new Request()
                        .withResponse_time(100L)
                        .withRequest_executed_at(LocalDateTime.now())
                        .withUrl("https://simple-scala-api.herokuapp.com/api/1")
        );

        Request request = save.block();
        Assertions.assertNotNull(request);
        Request persistedRequest = requestRepository
                .findById(request.getRequest_id())
                .block();

        Assertions.assertNotNull(persistedRequest);
    }

    @Test
    void testRequestStats() {
        List<RequestStatsView> requestStats = requestRepository.findRequestStats().collectList().block();
        Assertions.assertNotNull(requestStats);
        Assertions.assertFalse(requestStats.isEmpty());
        System.out.println(requestStats);
    }
}
