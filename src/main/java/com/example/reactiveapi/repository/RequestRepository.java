package com.example.reactiveapi.repository;

import com.example.reactiveapi.domain.Request;
import com.example.reactiveapi.dto.RequestStatsView;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface RequestRepository extends ReactiveCrudRepository<Request, Long > {

    @Query(value = "SELECT r.url,\n" +
            "       avg(r.response_time) average_response_time\n" +
            "FROM request r\n" +
            "where r.request_executed_at > current_date\n" +
            "GROUP BY r.url")
    Flux<RequestStatsView> findRequestStats();
}
