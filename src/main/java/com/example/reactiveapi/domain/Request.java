package com.example.reactiveapi.domain;


import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@Builder
@With
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    @Id
    private Long request_id;
    private String url;
    private LocalDateTime request_executed_at;
    private Long response_time;
}
