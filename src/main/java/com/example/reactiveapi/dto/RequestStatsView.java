package com.example.reactiveapi.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@With
@Builder
public class RequestStatsView {
    private String url;
    private Long average_response_time;
}
